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
public class ViewportUpdater implements CalculationNotifier {
    private static final int DELAY = 300;
    private static final int MAX_CONCURRENCY = Runtime.getRuntime().availableProcessors();

    private final ThreadFactory factory;

    private Thread preemptiveEngineCalculationThread;
    private Thread delayedCalculationThread;

    private final EngineCalculationTask engineCalculationTask;
    private final PreemptiveEngineCalculationTask preemptiveEngineCalculationTask;
    private final DelayedCalculationTask delayedCalculationTask;

    private RunnableExecutorPool pool;

    private volatile CalculationResults results = null;

    private Lock startStopLock = new ReentrantLock();

    private final ViewportChangedListener listener;

    public ViewportUpdater(ThreadFactory factory, ViewportChangedListener listener) {
        this.factory = factory;
        this.listener = listener;

        CalculationEngine engine = new VectorCalculationEngine();

        engineCalculationTask = new EngineCalculationTask(engine, this);
        preemptiveEngineCalculationTask = new PreemptiveEngineCalculationTask(engineCalculationTask);
        delayedCalculationTask = new DelayedCalculationTask(preemptiveEngineCalculationTask,
                DELAY);
    }

    public void start() {
        startStopLock.lock();
        try {
            pool = new RunnableExecutorPool(MAX_CONCURRENCY,
                    factory,
                    RunnableExecutorFactories.SYNCHRONOUS);
            pool.start();

            preemptiveEngineCalculationThread = factory.newThread(preemptiveEngineCalculationTask);
            preemptiveEngineCalculationThread.setName("Preemptive engine calculation thread");
            preemptiveEngineCalculationThread.start();

            delayedCalculationThread = factory.newThread(delayedCalculationTask);
            delayedCalculationThread.setName("Delayed calculation thread");
            delayedCalculationThread.start();
        } finally {
            startStopLock.unlock();
        }
    }

    public void stop() {
        startStopLock.lock();
        try {
            boolean interrupted = false;

            preemptiveEngineCalculationThread.interrupt();
            try {
                preemptiveEngineCalculationThread.join();
            } catch (InterruptedException e) {
                interrupted = true;
            }

            delayedCalculationThread.interrupt();
            try {
                delayedCalculationThread.join();
            } catch (InterruptedException e) {
                interrupted = true;
            }

            pool.interrupt();
            try {
                pool.join();
            } catch (InterruptedException e) {
                interrupted = true;
            }

            preemptiveEngineCalculationThread = null;
            delayedCalculationThread = null;
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
        delayedCalculationTask.calculate(parameters);
    }

    public void paint(Graphics g, int width, int height) {
        CalculationResults lastResults = results;

        if (lastResults == null) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            return;
        }
        CalculationParameters params = lastResults.getParameters();
        RectRange range = new RectRange(0, 0,
                params.getWidth(),
                params.getHeight());
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


    @Override
    public void doneCalculation(CalculationResults results) {
        this.results = results;
        listener.viewportChanged();
    }
}
