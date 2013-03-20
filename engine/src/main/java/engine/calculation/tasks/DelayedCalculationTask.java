package engine.calculation.tasks;

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
                task.calculate(newParams);
            }
        } catch (InterruptedException e) {
            // return
        }
    }


    public void calculate(CalculationParameters parameters) {
        queue.put(parameters);
    }
}
