package util;

/**
 * User: Oleksiy Pylypenko
 * At: 3/19/13  4:52 PM
 */
public class CanceledException extends RuntimeException {
    private boolean interrupted;

    public CanceledException(boolean interrupted) {
        this.interrupted = interrupted;
    }

    public boolean isInterrupted() {
        return interrupted;
    }
}