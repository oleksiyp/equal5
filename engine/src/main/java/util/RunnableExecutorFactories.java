package util;

/**
 * User: Oleksiy Pylypenko
 * At: 3/15/13  1:06 PM
 */
public class RunnableExecutorFactories {
    public static final RunnableExecutorFactory SYNCHRONOUS = new SynchronousFactory();

    public static final RunnableExecutorFactory QUEUED = new QueuedFactory();

    private static class SynchronousFactory implements RunnableExecutorFactory {
        @Override
        public RunnableExecutor newRunnableExecutor() {
            return RunnableExecutors.newSynchronousExecutor();
        }
    }

    private static class QueuedFactory implements RunnableExecutorFactory {
        @Override
        public RunnableExecutor newRunnableExecutor() {
            return RunnableExecutors.newQueuedExecutor();
        }
    }
}
