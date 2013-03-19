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

    public void cancel() throws InterruptedException {
        cancel = true;
    }

    public void checkCanceled() throws CanceledException {
        if (Thread.interrupted()) {
            throw new CanceledException(true);
        }
        if (cancel) {
            reset();
            throw new CanceledException(true);
        }
    }

    public void reset() {
        cancel = false;
    }
}
