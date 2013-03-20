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

    public CalculationParameters(ViewportBounds bounds,
                                 ViewportSize size,
                                 Equation ...equations) {
        if (bounds == null) {
            throw new NullPointerException("bounds");
        }
        if (size == null) {
            throw new NullPointerException("bounds");
        }
        if (equations == null) {
            throw new NullPointerException("equations");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalculationParameters that = (CalculationParameters) o;

        if (!bounds.equals(that.bounds)) return false;
        if (!Arrays.equals(equations, that.equations)) return false;
        if (!size.equals(that.size)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(equations);
        result = 31 * result + bounds.hashCode();
        result = 31 * result + size.hashCode();
        return result;
    }
}
