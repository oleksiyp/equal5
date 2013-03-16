package gui.mainapp;

import engine.expressions.Equation;
import gui.util.CountDownQueue;
import util.RunnableExecutorFactories;
import util.RunnableExecutorPool;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: Oleksiy Pylypenko
 * At: 3/15/13  12:17 PM
 */
public class ViewportUpdater {
    public static final int DEFAULT_DELAY = 3000;

    private static final int MAX_CONCURRENCY = Runtime.getRuntime().availableProcessors();

    private final ThreadFactory factory;

    private CountDownQueue<Parameters> queue = new CountDownQueue<Parameters>();
    {
        queue.setCountDownTime(DEFAULT_DELAY);
    }

    private Updater updater;
    private Thread updaterThread;
    private RunnableExecutorPool pool;

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

            updaterThread = factory.newThread(new Updater());
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

    private class Updater implements Runnable {
        private static final long DELAY = 3000;

        // general algorithm should following

        // there is such kind of parameters
        //
        // current
        // inProcess
        // newOne
        //
        // setParamters(newOne) {
        //   if (newOne equals current) {
        //     if (no inProcess) { skip } else { cancel inProcess }
        //   } else {
        //     if (newOne equalsIgnoreWidthHeight current) {
        //         triggerRepaint()
        //     }
        //     start countdown on newOne
        //   }
        // }
        // play() {
        //    if (playing) return
        //    playing = current
        //    t = 0
        //    nextFrame()
        // }
        // nextFrame() {
        //    if (t >= steps) { playing = null; finnishPlaying(); }
        //    playing = playing with t
        //    run playing
        //    t++
        // }
        // stopPlaying() {
        //    playing = null
        // }
        // on countdown done {
        //    if (playing) return
        //    if (newOne equals inProcess) { skip }
        //    if (there is inProcess) {
        //       cancel inProcess
        //    }
        //    run newOne
        // }
        // on inProcess done {
        //    current = inProcess
        //    triggerRepaint()
        //    if (playing) {
        //        nextFrame()
        //    }
        // }
        //

        @Override
        public void run() {
            Parameters newParams;
            try {
                while ((newParams = queue.take()) != null) {

                }
            } catch (InterruptedException e) {
                // return
            }
        }
    }

    public void setParameters(Parameters parameters) {
        queue.put(parameters);
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
