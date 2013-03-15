package util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;

/**
 * User: Oleksiy Pylypenko
 * At: 3/15/13  12:31 PM
 */
public class RunnableExecutors {
    public static RunnableExecutor newSynchronousExecutor() {
        return new QueueRunnableExecutor(new SynchronousQueue<Runnable>());
    }

    public static RunnableExecutor newSynchronousExecutor(RuntimeExceptionReporter reporter) {
        return new QueueRunnableExecutor(new SynchronousQueue<Runnable>(), reporter);
    }

    public static RunnableExecutor newQueuedExecutor(int bound) {
        return new QueueRunnableExecutor(new LinkedBlockingDeque<Runnable>(bound));
    }

    public static RunnableExecutor newQueuedExecutor(int bound, RuntimeExceptionReporter reporter) {
        return new QueueRunnableExecutor(new LinkedBlockingDeque<Runnable>(bound), reporter);
    }

    public static RunnableExecutor newQueuedExecutor() {
        return new QueueRunnableExecutor(new LinkedBlockingDeque<Runnable>());
    }

    public static RunnableExecutor newQueuedExecutor(RuntimeExceptionReporter reporter) {
        return new QueueRunnableExecutor(new LinkedBlockingDeque<Runnable>(), reporter);
    }
}
