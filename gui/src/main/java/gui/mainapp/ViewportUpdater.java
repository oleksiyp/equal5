package gui.mainapp;

import engine.calculation.CalculationEngine;
import engine.calculation.VectorCalculationEngine;
import engine.calculation.tasks.*;
import engine.locus.DrawToImage;
import engine.locus.PixelDrawable;
import engine.locus.RectRange;
import util.RunnableExecutorFactories;
import util.RunnableExecutorPool;

import java.awt.*;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: Oleksiy Pylypenko
 * At: 3/15/13  12:17 PM
 */
public class ViewportUpdater {
    private static final int DELAY = 25;
    private static final int MAX_CONCURRENCY = Runtime.getRuntime().availableProcessors();

    private final ThreadFactory factory;

    private Thread preemptiveCalcThread;
    private Thread delayedCalcThread;

    private final EngineCalculationTask engineCalcTask;
    private final PreemptiveCalculationTask preemptiveCalcTask;
    private final DelayedCalculationTask delayedCalcTask;

    private RunnableExecutorPool pool;

    private volatile CalculationResults results = null;

    private Lock startStopLock = new ReentrantLock();

    private final ViewportChangedListener listener;

    public ViewportUpdater(ThreadFactory factory, ViewportChangedListener listener) {
        this.factory = factory;
        this.listener = listener;

        CalculationEngine engine = new VectorCalculationEngine();

        engineCalcTask = new EngineCalculationTask(engine, new DoneCalculationHandler());
        preemptiveCalcTask = new PreemptiveCalculationTask(engineCalcTask);
        delayedCalcTask = new DelayedCalculationTask(preemptiveCalcTask, DELAY);
    }

    public void start() {
        startStopLock.lock();
        try {
            pool = new RunnableExecutorPool(MAX_CONCURRENCY,
                    factory,
                    RunnableExecutorFactories.SYNCHRONOUS);
            pool.start();

            preemptiveCalcThread = factory.newThread(preemptiveCalcTask);
            preemptiveCalcThread.setName("Preemptive engine calculation thread");
            preemptiveCalcThread.start();

            delayedCalcThread = factory.newThread(delayedCalcTask);
            delayedCalcThread.setName("Delayed calculation thread");
            delayedCalcThread.start();
        } finally {
            startStopLock.unlock();
        }
    }

    public void stop() {
        startStopLock.lock();
        try {
            boolean interrupted = false;

            preemptiveCalcThread.interrupt();
            try {
                preemptiveCalcThread.join();
            } catch (InterruptedException e) {
                interrupted = true;
            }

            delayedCalcThread.interrupt();
            try {
                delayedCalcThread.join();
            } catch (InterruptedException e) {
                interrupted = true;
            }

            pool.interrupt();
            try {
                pool.join();
            } catch (InterruptedException e) {
                interrupted = true;
            }

            preemptiveCalcThread = null;
            delayedCalcThread = null;
            pool = null;

            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        } finally {
            startStopLock.unlock();
        }
    }

    public void setParameters(CalculationParameters parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("parameters");
        }
        CalculationResults lastResults = results;
        if (lastResults != null && lastResults.getParameters().equals(parameters)) {
            // skip the same
            return;
        }
        delayedCalcTask.calculate(parameters);
    }

    public void paint(Graphics g, int width, int height) {
        CalculationResults lastResults = results;

        if (lastResults == null) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            return;
        }

        CalculationParameters params = lastResults.getParameters();

        RectRange range = RectRange.fromViewportSize(params.getSize());
        DrawToImage drawToImage = new DrawToImage(range);
        PixelDrawable[] drawables = lastResults.getDrawables();
        for (PixelDrawable drawable : drawables) {
            RectRange size = drawable.getSize();
            drawable.draw(size, drawToImage);
        }

        g.drawImage(drawToImage.getImage(),
                0, 0, width, height,
                0, 0, range.getWidth(), range.getHeight(),
                null);
    }

    protected void doneCalculation(CalculationResults results) {
        this.results = results;
        listener.viewportChanged();
    }

    private class DoneCalculationHandler implements CalculationNotifier {
        @Override
        public void doneCalculation(CalculationResults results) {
            ViewportUpdater.this.doneCalculation(results);
        }
    }
}
