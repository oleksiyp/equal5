package engine.calculation.vector.opeartions;

import engine.calculation.functions.MathFunction;

/**
 * User: Oleksiy Pylypenko
 * At: 3/13/13  4:37 PM
 */
public class MathVectorOperation extends VectorOperation {
    private MathFunction.Type type;
    private int[] slots;
    private int resultSlot;

    public MathVectorOperation(MathFunction.Type type, int[] slots, int resultSlot) {
        this.type = type;
        this.slots = slots;
        this.resultSlot = resultSlot;
    }

    @Override
    public void apply(int size, double[][] data) {
        type.accept(new MathVectorCalc(size, data));
    }

    @Override
    public boolean applicable(boolean[] calculated) {
        for (int slot : slots) {
            if (!calculated[slot]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void markCalculated(boolean[] calculatedSlots) {
        for (int slot : slots) {
            calculatedSlots[slot] = true;
        }
    }

    private class MathVectorCalc implements MathFunction.TypeVisitor {
        private int size;
        private double[][] data;

        public MathVectorCalc(int size, double[][] data) {
            this.size = size;
            this.data = data;
        }

        @Override
        public void sin() {
            for (int i = 0; i < size; i++) {
                data[resultSlot][i] = Math.sin(data[slots[0]][i]);
            }
        }

        @Override
        public void cos() {
            for (int i = 0; i < size; i++) {
                data[resultSlot][i] = Math.cos(data[slots[0]][i]);
            }
        }
    }
}
