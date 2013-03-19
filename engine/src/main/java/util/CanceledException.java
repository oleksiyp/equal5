package util;

/**
 * User: Oleksiy Pylypenko
 * At: 3/19/13  4:52 PM
 */
public abstract class CanceledException extends RuntimeException {
    private boolean interrupt;

    public CanceledException(boolean interrupt) {
        this.interrupt = interrupt;
    }

    public abstract void cancellationDone();
}
