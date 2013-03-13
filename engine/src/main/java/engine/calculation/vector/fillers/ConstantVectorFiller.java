package engine.calculation.vector.fillers;

import java.util.Arrays;

/**
* User: Oleksiy Pylypenko
* At: 3/13/13  1:03 PM
*/
public class ConstantVectorFiller implements VectorFiller {
    private double value;

    public ConstantVectorFiller(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public void fill(double[] vector) {
        Arrays.fill(vector, value);
    }
}
