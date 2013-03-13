package engine.calculation.vector;

import com.google.common.base.Stopwatch;
import engine.calculation.vector.opeartions.VectorOperation;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * User: Oleksiy Pylypenko
 * At: 3/12/13  6:38 PM
 */
public final class SequentialVectorMachine implements VectorMachine {
    private final int slotCount;

    private final int[] resultSlots;
    private final Map<String, Integer> argumentSlots;

    private final Map<Integer, Double> constantSlots;
    private final VectorOperation []operations;

    SequentialVectorMachine(int slotCount,
                            int[] resultSlots,
                            VectorOperation[] operations,
                            Map<String, Integer> argumentSlots,
                            Map<Integer, Double> constantSlots) {
        this.slotCount = slotCount;
        this.resultSlots = resultSlots;
        this.argumentSlots = argumentSlots;
        this.constantSlots = constantSlots;
        this.operations = operations;
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

    public State newState(int size) {
        return new StateImpl(size);
    }

    public final class StateImpl implements State {
        private final double [][] vectors;
        private final int size;

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
        public double []get(int slot) {
            return vectors[slot];
        }

        @Override
        public void applyOperations() {
            for (VectorOperation operation : operations) {
                operation.apply(size, vectors);
            }
        }

        @Override
        public void applyAndEstimateOperations(TimeReporter timeReporter) {
            Stopwatch sw = new Stopwatch();

            for (VectorOperation operation : operations) {
                sw.reset().start();

                operation.apply(size, vectors);

                double time = sw.stop().elapsedTime(TimeUnit.MICROSECONDS);
                time /= 1000;

                timeReporter.report(operation.toString(), size, time, 0);
            }
        }
    }


}
