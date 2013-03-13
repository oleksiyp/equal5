package engine.calculation.vector.fillers;

import engine.calculation.vector.VectorFiller;

/**
* User: Oleksiy Pylypenko
* At: 3/13/13  1:03 PM
*/
public class LinearVectorFiller implements VectorFiller {
    private double start;
    private double delta;

    public LinearVectorFiller(double start, double delta) {
        this.start = start;
        this.delta = delta;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    @Override
    public void fill(double[] vector) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = start + i * delta;
        }
    }
}
