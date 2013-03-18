package gui.mainapp;

import engine.expressions.Equation;
import gui.util.CountDownQueue;
import util.CancelFlag;
import util.RunnableExecutorFactories;
import util.RunnableExecutorPool;

import java.awt.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: Oleksiy Pylypenko
 * At: 3/15/13  12:17 PM
 */
public class ViewportUpdater {

    private static final int MAX_CONCURRENCY = Runtime.getRuntime().availableProcessors();

    private final ThreadFactory factory;

    private DelayedUpdater delayedUpdater;
    private Thread updaterThread;
    private RunnableExecutorPool pool;

    private volatile Parameters current = null;

    private Lock startStopLock = new ReentrantLock();

    public ViewportUpdater(ThreadFactory factory) {
        this.factory = factory;
    }

    public void start() {
        startStopLock.lock();
        try {
            pool = new RunnableExecutorPool(MAX_CONCURRENCY,
                    factory,
                    RunnableExecutorFactories.SYNCHRONOUS);
            pool.start();

            updaterThread = factory.newThread(new DelayedUpdater());
            updaterThread.setName("Viewport updater thread");
            updaterThread.start();
        } finally {
            startStopLock.unlock();
        }
    }

    public void stop() {
        startStopLock.lock();
        try {
            boolean interrupted = false;

            updaterThread.interrupt();
            try {
                updaterThread.join();
            } catch (InterruptedException e) {
                interrupted = true;
            }

            pool.interrupt();
            try {
                pool.join();
            } catch (InterruptedException e) {
                interrupted = true;
            }

            updaterThread = null;
            pool = null;

            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        } finally {
            startStopLock.unlock();
        }
    }

    private Calculation calculation = new Calculation();

    private CancelFlag flag = new CancelFlag();

    private class Calculation implements Runnable {

        private Calculation() {
        }

        @Override
        public void run() {

        }

        public void startAndCancel(Parameters parameters)
                throws InterruptedException {
        }
    }

    private class DelayedUpdater implements Runnable {
        private static final long DELAY = 3000;

        public static final int DEFAULT_DELAY = 3000;
        private final CountDownQueue<Parameters> queue = new CountDownQueue<Parameters>();
        {
            queue.setCountDownTime(DEFAULT_DELAY);
        }

        @Override
        public void run() {
            Parameters newParams;
            try {
                while ((newParams = queue.take()) != null) {
                    Parameters cur = current;
                    if (newParams.equals(cur)) {
                        continue;
                    }
                    calculation.startAndCancel(newParams);
                }
            } catch (InterruptedException e) {
                // return
            }
        }

        private void startExecution(Parameters newParams) {

        }

        private void cancelExecution() {

        }

        public void put(Parameters parameters) {
            queue.put(parameters);
        }
    }

    public void setParameters(Parameters parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("parameters");
        }
        delayedUpdater.put(parameters);
    }

    public void paint(Graphics g) {

    }


    private class Parameters {
        private final Equation equation;
        private final ViewportBounds bounds;
        private final int width, height;

        private Parameters(Equation equation, ViewportBounds bounds, int width, int height) {
            this.equation = equation;
            this.bounds = bounds;
            this.width = width;
            this.height = height;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Parameters that = (Parameters) o;

            if (height != that.height) return false;
            if (width != that.width) return false;
            if (!bounds.equals(that.bounds)) return false;
            if (!equation.equals(that.equation)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = equation.hashCode();
            result = 31 * result + bounds.hashCode();
            result = 31 * result + width;
            result = 31 * result + height;
            return result;
        }
    }
}
