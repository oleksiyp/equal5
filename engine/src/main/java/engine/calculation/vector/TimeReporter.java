package engine.calculation.vector;

/**
 * User: Oleksiy Pylypenko
 * At: 3/12/13  8:56 PM
 */
public interface TimeReporter {
    void report(String operation, int size, double ms, int nRunner);
}
