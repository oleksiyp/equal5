package engine.calculation.vector.opeartions;

/**
 * User: Oleksiy Pylypenko
 * At: 3/12/13  7:23 PM
 */
public class PowerVectorOperation extends VectorOperation {
    private final int leftSlot;
    private final int rightSlot;
    private final int resultSlot;

    public PowerVectorOperation(int leftSlot,
                                int rightSlot,
                                int resultSlot) {
        super();
        this.leftSlot = leftSlot;
        this.rightSlot = rightSlot;
        this.resultSlot = resultSlot;
    }

    @Override
    public void apply(int size, double[][] data) {
        for (int i = 0; i < size; i++) {
            data[resultSlot][i] = Math.pow(data[leftSlot][i], data[rightSlot][i]);
        }
    }

    @Override
    public boolean applicable(boolean[] calculated) {
        return calculated[leftSlot] && calculated[rightSlot];
    }

    @Override
    public void markCalculated(boolean[] calculatedSlots) {
        calculatedSlots[resultSlot] = true;
    }

    @Override
    public String toString() {
        return "pow(" + leftSlot + ", " + rightSlot + ") => " + resultSlot;
    }
}