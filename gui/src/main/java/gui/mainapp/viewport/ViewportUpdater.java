package gui.mainapp.viewport;

import engine.calculation.*;
import engine.calculation.tasks.*;
import engine.calculation.vector.implementations.VectorMachineBuilder;
import engine.calculation.vector.VectorMachineEvaluator;
import engine.calculation.drawables.DrawToImage;
import engine.calculation.drawables.Drawable;
import engine.calculation.drawables.RectRange;

import javax.swing.*;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: Oleksiy Pylypenko
 * At: 3/15/13  12:17 PM
 */
class ViewportUpdater {
    private static final int DELAY = 25;
    private static final int MAX_CONCURRENCY = Runtime.getRuntime().availableProcessors();

    private final ThreadFactory factory;

    private final DrawToImage drawToImage = new DrawToImage(new RectRange(0, 0, 500, 500));

    private final DelayedCalculationTask delayedCalcTask;
    private final FrameDoneRunnable frameDoneRunnable = new FrameDoneRunnable();

    private int concurrency = MAX_CONCURRENCY;

    private ExecutorService executor;
    private Thread delayedCalcThread;

    private volatile CalculationResults results = null;

    private Lock startStopLock = new ReentrantLock();

    private final VectorMachineBuilder vmBuilder;

    private final List<FrameListener> frameListeners = new ArrayList<FrameListener>();

    public ViewportUpdater(ThreadFactory factory) {
        this.factory = factory;

        vmBuilder = new VectorMachineBuilder();
        VectorMachineEvaluator evaluator = new VectorMachineEvaluator(vmBuilder);
        CalculationEngine engine = new VectorCalculationEngine(evaluator);

        EngineCalculationTask engineCalcTask = new EngineCalculationTask(engine, new DoneCalculationHandler());
        delayedCalcTask = new DelayedCalculationTask(engineCalcTask, DELAY);
    }

    public void start() {
        startStopLock.lock();
        try {
            executor = Executors.newFixedThreadPool(
                    MAX_CONCURRENCY,
                    factory);

            vmBuilder.setConcurrency(concurrency, executor);

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

            delayedCalcThread.interrupt();
            try {
                delayedCalcThread.join();
            } catch (InterruptedException e) {
                interrupted = true;
            }

            executor.shutdown();

            delayedCalcThread = null;
            executor = null;

            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        } finally {
            startStopLock.unlock();
        }
    }

    public void setParameters(CalculationParameters parameters) {
        setParameters(parameters, false, true);
    }

    public void setParameters(CalculationParameters parameters,
                              boolean recalc,
                              boolean delayed) {
        if (parameters == null) {
            throw new IllegalArgumentException("parameters");
        }
        if (!recalc) {
            CalculationResults lastResults = results;
            if (lastResults != null && lastResults.getParameters().equals(parameters)) {
                // skip the same
                return;
            }
        }
        delayedCalcTask.calculate(delayed ? DELAY : 0, parameters);
    }

    public CalculationResults getResults() {
        return results;
    }

    public void paint(Graphics g, int width, int height) {
        CalculationResults lastResults = results;

        if (lastResults == null) {
            return;
        }

        CalculationParameters params = lastResults.getParameters();

        RectRange range = RectRange.fromViewportSize(params.getSize());
        drawToImage.resize(range);

        Drawable[] drawables = lastResults.getDrawables();
        for (Drawable drawable : drawables) {
            RectRange size = drawable.getSize();
            drawToImage.draw(size, drawable);
        }

        g.drawImage(drawToImage.getImage(),
                0, 0, width, height,
                0, 0, range.getWidth(), range.getHeight(),
                null);
    }

    protected void doneCalculation(CalculationResults results) {
        this.results = results;
        SwingUtilities.invokeLater(frameDoneRunnable);
    }

    public void addFrameListener(FrameListener listener) {
        synchronized (frameListeners) {
            frameListeners.add(listener);
        }
    }

    public void removeFrameListener(FrameListener listener) {
        synchronized (frameListeners) {
            frameListeners.remove(listener);
        }
    }

    private class DoneCalculationHandler implements CalculationNotifier {
        @Override
        public void doneCalculation(CalculationResults results) {
            ViewportUpdater.this.doneCalculation(results);
        }

        @Override
        public void runtimeProblem(RuntimeException ex) {
            System.err.println(ex);
        }
    }

    private class FrameDoneRunnable implements Runnable {
        @Override
        public void run() {
            List<FrameListener> listeners;
            synchronized (frameListeners) {
                listeners = new ArrayList<FrameListener>(frameListeners);
            }

            for (FrameListener listener : listeners) {
                listener.frameDone();
            }
        }
    }
}
