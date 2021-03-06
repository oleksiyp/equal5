package engine.calculation.tasks;

import engine.calculation.CalculationParameters;
import util.CountDownQueue;

/**
* User: Oleksiy Pylypenko
* At: 3/19/13  6:12 PM
*/
public class DelayedCalculationTask implements CalculationTask, Runnable {
    private CalculationTask task;
    private final CountDownQueue<CalculationParameters> queue = new CountDownQueue<CalculationParameters>();

    public DelayedCalculationTask(CalculationTask task, int delay) {
        this.task = task;
        queue.setCountDownTime(delay);
    }

    @Override
    public void run() {
        CalculationParameters newParams;
        try {
            while ((newParams = queue.take()) != null) {
                try {
                    task.calculate(newParams);
                } catch (RuntimeException ex) {
                    // TODO : add logging
                }
            }
        } catch (InterruptedException e) {
            // return
        }
    }


    public void calculate(CalculationParameters parameters) {
        queue.put(parameters);
    }

    public void calculate(long delay, CalculationParameters parameters) {
        queue.put(delay, parameters);
    }
}
