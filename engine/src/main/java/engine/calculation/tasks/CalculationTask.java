package engine.calculation.tasks;

import engine.calculation.CalculationParameters;

/**
 * User: Oleksiy Pylypenko
 * At: 3/19/13  6:11 PM
 */
public interface CalculationTask {
    void calculate(CalculationParameters parameters)
            throws InterruptedException;
}
