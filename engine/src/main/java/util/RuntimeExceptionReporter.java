package util;

/**
 * User: Oleksiy Pylypenko
 * At: 3/15/13  12:29 PM
 */
public interface RuntimeExceptionReporter {
    void exceptionThrown(Runnable runnable, RuntimeException ex);
}
