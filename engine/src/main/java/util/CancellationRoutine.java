package util;

/**
 * User: Oleksiy Pylypenko
 * At: 3/19/13  5:17 PM
 */
public interface CancellationRoutine {
    void checkCanceled() throws CanceledException;

    CancellationRoutine NO_ROUTINE = new NoCancellationRoutine();

    class NoCancellationRoutine implements CancellationRoutine {
        @Override
        public void checkCanceled() throws CanceledException {
        }
    }
}
