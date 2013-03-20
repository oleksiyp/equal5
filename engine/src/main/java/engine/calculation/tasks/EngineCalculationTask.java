package engine.calculation.tasks;

import engine.calculation.CalculationEngine;
import engine.calculation.CalculationParameters;
import engine.calculation.CalculationResults;
import util.Cancelable;
import util.CancellationRoutine;

/**
 * User: Oleksiy Pylypenko
 * At: 3/20/13  2:14 PM
 */
public class EngineCalculationTask implements CalculationTask, Cancelable {
    private final CalculationEngine engine;
    private final CalculationNotifier notifier;

    public EngineCalculationTask(CalculationEngine engine, CalculationNotifier notifier) {
        this.engine = engine;
        this.notifier = notifier;
    }

    @Override
    public void calculate(CalculationParameters params) throws InterruptedException {
        try {
            CalculationResults results = engine.calculate(params);
            notifier.doneCalculation(results);
        } catch (RuntimeException ex) {
            notifier.runtimeProblem(ex);
        }
    }

    @Override
    public void setCancellationRoutine(CancellationRoutine routine) {
        if (this.engine instanceof Cancelable) {
            ((Cancelable)this.engine).setCancellationRoutine(routine);
        }
    }
}
