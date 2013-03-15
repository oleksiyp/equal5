package util;

import java.util.concurrent.BlockingQueue;

/**
* User: Oleksiy Pylypenko
* At: 3/15/13  12:27 PM
*/
public class QueueRunnableExecutor implements RunnableExecutor {
    private final BlockingQueue<Runnable> queue;
    private RuntimeExceptionReporter reporter;

    public QueueRunnableExecutor(BlockingQueue<Runnable> queue) {
        this(queue, null);
    }

    public QueueRunnableExecutor(BlockingQueue<Runnable> queue, RuntimeExceptionReporter reporter) {
        this.queue = queue;
        this.reporter = reporter;
    }

    @Override
    public void execute(Runnable runnable) throws InterruptedException {
        queue.put(runnable);
    }

    @Override
    public void run() {
        while (Thread.currentThread().isInterrupted()) {
            try {
                Runnable runnable = queue.take();
                try {
                    runnable.run();
                } catch (RuntimeException ex) {
                    if (reporter != null) {
                        reporter.exceptionThrown(runnable, ex);
                    }
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
