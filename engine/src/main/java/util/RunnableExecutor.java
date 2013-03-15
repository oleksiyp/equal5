package util;

/**
 * User: Oleksiy Pylypenko
 * At: 3/15/13  12:26 PM
 */
public interface RunnableExecutor extends Runnable {
    void execute(Runnable runnable)
            throws InterruptedException;
}
