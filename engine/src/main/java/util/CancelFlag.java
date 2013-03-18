package util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/18/13
 * Time: 9:54 AM
 */
public class CancelFlag {
    private volatile boolean cancel = false;

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private boolean cancelDone;

    public void cancel() {
        cancel = true;
    }

    public void awaitCanceled() throws InterruptedException {
        lock.lock();
        try {
            while (cancelDone) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public void canceled() {
        lock.lock();
        try {
            cancelDone = true;
        } finally {
            lock.unlock();
        }
    }

    public void reset() {
        cancel = false;
        lock.lock();
        try {
            cancelDone = false;
        } finally {
            lock.unlock();
        }
    }
}
