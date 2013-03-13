package engine.calculation.vector.opeartions;

/**
 * User: Oleksiy Pylypenko
 * At: 3/12/13  7:23 PM
 */
public class SubtractionVectorOperation extends VectorOperation {
    private int leftSlot;
    private int rightSlot;
    private int resultSlot;

    public SubtractionVectorOperation(int leftSlot,
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
            data[resultSlot][i] = data[leftSlot][i] - data[rightSlot][i];
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
        return "sub(" + leftSlot + ", " + rightSlot + ") => " + resultSlot;
    }
}