package engine.calculation.tasks;

import engine.calculation.CalculationEngine;
import engine.locus.PixelDrawable;
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
        engine.setSize(params.getWidth(), params.getHeight());
        //params.bounds
        PixelDrawable[] drawables = engine.calculate(params.getEquation());
        CalculationResults results = new CalculationResults(params, drawables);
        notifier.doneCalculation(results);
    }

    @Override
    public void setCancellationRoutine(CancellationRoutine routine) {
        this.engine.setCancellationRoutine(routine);
    }
}
