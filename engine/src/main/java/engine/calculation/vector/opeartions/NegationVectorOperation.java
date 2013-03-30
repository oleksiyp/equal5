package engine.calculation.vector.opeartions;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/30/13
 * Time: 3:11 PM
 */
public class NegationVectorOperation extends VectorOperation {
    private final int opSlot;
    private final int resultSlot;

    public NegationVectorOperation(int opSlot, int resultSlot) {
        super();
        this.opSlot = opSlot;
        this.resultSlot = resultSlot;
    }

    @Override
    public void apply(int size, double[][] data) {
        for (int i = 0; i < size; i++) {
            data[resultSlot][i] = -data[opSlot][i];
        }
    }

    @Override
    public boolean applicable(boolean[] calculated) {
        return calculated[opSlot];
    }

    @Override
    public void markCalculated(boolean[] calculatedSlots) {
        calculatedSlots[resultSlot] = true;
    }

    @Override
    public String toString() {
        return "neg(" + opSlot + ") => " + resultSlot;
    }
}
