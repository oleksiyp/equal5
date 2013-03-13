package engine.calculation.vector.opeartions;

/**
 * User: Oleksiy Pylypenko
 * At: 3/12/13  6:39 PM
 */
public abstract class VectorOperation {
    public abstract void apply(int size, double[][] data);

    public abstract boolean applicable(boolean[] calculated);

    public abstract void markCalculated(boolean[] calculatedSlots);
}
