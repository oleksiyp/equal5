package engine.calculation.tasks;

import engine.calculation.CalculationEngine;
import engine.locus.PixelDrawable;
import util.CancelFlag;
import util.CanceledException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
* User: Oleksiy Pylypenko
* At: 3/19/13  6:10 PM
*/
public class EngineCalculationTask implements CalculationTask {
    private final CalculationEngine engine;

    private final CancelFlag cancelFlag = new CancelFlag();

    private final BlockingQueue<CalculationParameters> queue = new SynchronousQueue<CalculationParameters>();
    private final CalculationNotifier notifier;

    public EngineCalculationTask(CalculationEngine engine, CalculationNotifier notifier) {
        this.engine = engine;
        this.notifier = notifier;
        this.engine.setCancellationRoutine(cancelFlag);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try{
                    CalculationParameters params = queue.take();
                    cancelFlag.reset();

                    //params.bounds
                    engine.setSize(params.getWidth(), params.getHeight());
                    PixelDrawable[] drawables = engine.calculate(params.getEquation());

                    CalculationResults results = new CalculationResults(params, drawables);
                    notifier.doneCalculation(results);
                } catch (CanceledException ex) {
                    if (ex.isInterrupted()) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        } catch (InterruptedException e) {
            // return
        }
    }

    public void calculate(CalculationParameters parameters)
            throws InterruptedException {
        cancelFlag.cancel();
        queue.put(parameters);
    }
}
