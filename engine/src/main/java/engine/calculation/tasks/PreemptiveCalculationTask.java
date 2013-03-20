package engine.calculation.tasks;

import util.CancelFlag;
import util.Cancelable;
import util.CanceledException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
* User: Oleksiy Pylypenko
* At: 3/19/13  6:10 PM
*/
public class PreemptiveCalculationTask implements CalculationTask, Runnable {
    private final CancelFlag cancelFlag = new CancelFlag();
    private final BlockingQueue<CalculationParameters> queue = new SynchronousQueue<CalculationParameters>();
    private final CalculationTask nextChain;

    public PreemptiveCalculationTask(CalculationTask nextChain) {
        this.nextChain = nextChain;

        if (nextChain instanceof Cancelable) {
            ((Cancelable) nextChain).setCancellationRoutine(cancelFlag);
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try{
                    CalculationParameters params = queue.take();
                    cancelFlag.reset();

                    nextChain.calculate(params);

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
