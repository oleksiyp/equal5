package util;

import java.util.concurrent.ThreadFactory;

public class RunnableExecutorPool {
    private final Thread[] threads;
    private final RunnableExecutor[] executors;

    public RunnableExecutorPool(int count,
                                ThreadFactory factory,
                                RunnableExecutorFactory runnableExecutorFactory) {
        threads = new Thread[count];
        executors = new RunnableExecutor[count];

        for (int i = 0; i < count; i++) {
            executors[i] = runnableExecutorFactory.newRunnableExecutor();
            threads[i] = factory.newThread(executors[i]);
        }
    }

    public RunnableExecutor[] getExecutors() {
        return executors;
    }

    public void start() {
        for (Thread thread : threads) {
            thread.start();
        }
    }

    public void interrupt() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    public void join() throws InterruptedException {
        InterruptedException ie = null;

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                ie = e;
            }
        }

        if (ie != null) {
            throw ie;
        }
    }
}