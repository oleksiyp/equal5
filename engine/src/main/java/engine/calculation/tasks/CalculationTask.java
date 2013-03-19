package engine.calculation.tasks;

/**
 * User: Oleksiy Pylypenko
 * At: 3/19/13  6:11 PM
 */
public interface CalculationTask extends Runnable {
    void calculate(CalculationParameters parameters)
            throws InterruptedException;
}
