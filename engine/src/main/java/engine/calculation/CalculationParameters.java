package engine.calculation;

import engine.expressions.Equation;

import java.util.Arrays;

/**
* User: Oleksiy Pylypenko
* At: 3/19/13  6:07 PM
*/
public class CalculationParameters {
    private final Equation []equations;
    private final ViewportBounds bounds;
    private final ViewportSize size;
    private final double t;

    public CalculationParameters(ViewportBounds bounds,
                                 ViewportSize size,
                                 double t,
                                 Equation... equations) {
        this.t = t;
        if (bounds == null) {
            throw new NullPointerException("bounds");
        }
        if (size == null) {
            throw new NullPointerException("bounds");
        }
        if (equations == null) {
            throw new NullPointerException("equations");
        }
        if (t < 0 || t > 1) {
            throw new IllegalArgumentException("t");
        }

        this.equations = equations;
        this.bounds = bounds;
        this.size = size;
    }

    public Equation []getEquations() {
        return equations;
    }

    public ViewportBounds getBounds() {
        return bounds;
    }

    public ViewportSize getSize() {
        return size;
    }

    public double getT() {
        return t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalculationParameters that = (CalculationParameters) o;

        if (Double.compare(that.t, t) != 0) return false;
        if (!bounds.equals(that.bounds)) return false;
        if (!Arrays.equals(equations, that.equations)) return false;
        if (!size.equals(that.size)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = Arrays.hashCode(equations);
        result = 31 * result + bounds.hashCode();
        result = 31 * result + size.hashCode();
        temp = t != +0.0d ? Double.doubleToLongBits(t) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
