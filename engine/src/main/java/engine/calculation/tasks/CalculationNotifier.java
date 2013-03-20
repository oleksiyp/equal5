package engine.calculation.tasks;

import engine.calculation.CalculationResults;

/**
 * User: Oleksiy Pylypenko
 * At: 3/19/13  6:10 PM
 */
public interface CalculationNotifier {
    void doneCalculation(CalculationResults results);

    void runtimeProblem(RuntimeException ex);
}
