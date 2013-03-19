package util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/18/13
 * Time: 9:54 AM
 */
public class CancelFlag implements CancellationRoutine {
    private volatile boolean cancel = false;

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private boolean cancelDone;

    public void cancel() throws InterruptedException {
        cancel = true;

        lock.lock();
        try {
            while (cancelDone) {
                condition.await();
            }
            cancelDone = false;
        } finally {
            lock.unlock();
        }
    }

    public void checkCanceled() throws CanceledException {
        if (Thread.interrupted()) {
            throw new AcknowledgedCanceledException(true);
        }
        if (cancel) {
            cancel = false;
            throw new AcknowledgedCanceledException(true);
        }
    }

    private class AcknowledgedCanceledException extends CanceledException {
        public AcknowledgedCanceledException(boolean interrupt) {
            super(interrupt);
        }

        @Override
        public void cancellationDone() {
            lock.lock();
            try {
                cancelDone = true;
            } finally {
                lock.unlock();
            }
        }
    }
}
