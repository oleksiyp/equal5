package engine.calculation.vector;

import com.google.common.base.Stopwatch;
import engine.calculation.vector.opeartions.VectorOperation;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/12/13
 * Time: 11:56 PM
 */
public class ParallelVectorMachine implements VectorMachine {
    private final int slotCount;

    private final int[] resultSlots;
    private final Map<String, Integer> argumentSlots;

    private final Map<Integer, Double> constantSlots;
    private final VectorOperation[]operations;
    private final int concurrency;

    ParallelVectorMachine(int slotCount,
                            int[] resultSlots,
                            VectorOperation[] operations,
                            Map<String, Integer> argumentSlots,
                            Map<Integer, Double> constantSlots,
                            int concurrency) {
        if (concurrency < 1) {
            throw new IllegalArgumentException("concurrency");
        }

        this.slotCount = slotCount;
        this.resultSlots = resultSlots;
        this.argumentSlots = argumentSlots;
        this.constantSlots = constantSlots;
        this.operations = operations;
        this.concurrency = concurrency;
    }

    @Override
    public int getSlotCount() {
        return slotCount;
    }

    @Override
    public int[] getResultSlots() {
        return resultSlots;
    }

    @Override
    public Integer getArgumentSlot(String name) {
        return argumentSlots.get(name);
    }

    public VectorMachine.State newState(int size) {
        return new StateImpl(size);
    }

    public final class StateImpl implements VectorMachine.State {
        private final double [][] vectors;
        private int size;

        public StateImpl(int size) {
            this.size = size;
            this.vectors = new double[slotCount][size];
        }

        @Override
        public void init(boolean clear) {
            for (int slot : constantSlots.keySet()) {
                Double value = constantSlots.get(slot);
                Arrays.fill(vectors[slot], value);
            }

            if (clear) {
                for (int slot = 0; slot < slotCount; slot++) {
                    if (constantSlots.containsKey(slot)) {
                        continue;
                    }
                    Arrays.fill(vectors[slot], 0);
                }
            }
        }

        @Override
        public double[] get(int slot) {
            return vectors[slot];
        }

        @Override
        public void applyOperations() {
            applyAndEstimateOperations(null);
        }

        @Override
        public void applyAndEstimateOperations(TimeReporter timeReporter) {

            VectorOperationPool pool = new VectorOperationPool();
            Thread []threads = new Thread[concurrency];

            for (int i = 0; i < concurrency; i++) {
                Runnable runnable = timeReporter != null ?
                        new TRRunner(i, pool, timeReporter) :
                        new Runner(pool);
                threads[i] = new Thread(runnable);
            }

            for (Thread thread : threads) {
                thread.start();
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private class VectorOperationPool {
            private final Lock lock = new ReentrantLock(false);
            private final Condition condition = lock.newCondition();

            private final boolean [] calculatedSlots = new boolean[slotCount];
            private final boolean [] calculating = new boolean[operations.length];

            private int nCaclulated = 0;

            public VectorOperationPool() {
                for (int slot : constantSlots.keySet()) {
                    calculatedSlots[slot] = true;
                }
                for (int slot : argumentSlots.values()) {
                    calculatedSlots[slot] = true;
                }

                recalcNCalculated();
            }

            public int next() throws InterruptedException {
                lock.lock();
                try {
                    int applicable = -1;
                    while (nCaclulated < slotCount &&
                            (applicable = getApplicable()) == -1)
                    {
                        condition.await();
                    }
                    if (nCaclulated < slotCount) {
                        calculating[applicable] = true;

                        condition.signalAll();

                        return applicable;
                    }

                    condition.signalAll();
                } finally {
                    lock.unlock();
                }
                return -1;
            }

            private int getApplicable() {
                for (int i = 0; i < operations.length; i++) {
                    if (calculating[i]) {
                        continue;
                    }
                    VectorOperation op = operations[i];
                    if (op.applicable(calculatedSlots)) {
                        return i;
                    }
                }
                return -1;
            }

            public void done(int operation) {
                lock.lock();
                try {
                    operations[operation].markCalculated(calculatedSlots);

                    recalcNCalculated();
                } finally {
                    lock.unlock();
                }
            }

            private void recalcNCalculated() {
                nCaclulated = 0;
                for (boolean b : calculatedSlots) {
                    if (b) nCaclulated++;
                }
            }
        }

        private class Runner implements Runnable {
            private final VectorOperationPool pool;

            public Runner(VectorOperationPool pool) {
                this.pool = pool;
            }

            @Override
            public void run() {
                int op;
                try {
                    while ((op = pool.next()) != -1) {
                        try {
                            operations[op].apply(size, vectors);
                        } finally {
                            pool.done(op);
                        }
                    }
                } catch (InterruptedException e) {
                    // return
                }
            }
        }

        private class TRRunner implements Runnable {
            private final int nRunner;
            private final VectorOperationPool pool;
            private final TimeReporter timeReporter;

            public TRRunner(int nRunner, VectorOperationPool pool,
                            TimeReporter timeReporter) {
                this.nRunner = nRunner;
                this.pool = pool;
                this.timeReporter = timeReporter;
            }

            @Override
            public void run() {
                int op;
                Stopwatch sw = new Stopwatch();
                try {
                    while ((op = pool.next()) != -1) {
                        try {
                            VectorOperation operation = operations[op];

                            sw.reset().start();

                            operation.apply(size, vectors);

                            double time = sw.stop().elapsedTime(TimeUnit.MICROSECONDS);
                            time /= 1000;

                            timeReporter.report(operation.toString(), size, time, nRunner);
                        } finally {
                            pool.done(op);
                        }
                    }
                } catch (InterruptedException e) {
                    // skip
                }
            }
        }
    }
}
