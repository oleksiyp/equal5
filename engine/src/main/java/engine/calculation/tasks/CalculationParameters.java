package engine.calculation.tasks;

import engine.expressions.Equation;

/**
* User: Oleksiy Pylypenko
* At: 3/19/13  6:07 PM
*/
public class CalculationParameters {
    private final Equation equation;
    private final ViewportBounds bounds;
    private final int width, height;

    public CalculationParameters(Equation equation, ViewportBounds bounds, int width, int height) {
        this.equation = equation;
        this.bounds = bounds;
        this.width = width;
        this.height = height;
    }

    public Equation getEquation() {
        return equation;
    }

    public ViewportBounds getBounds() {
        return bounds;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalculationParameters that = (CalculationParameters) o;

        if (height != that.height) return false;
        if (width != that.width) return false;
        if (!bounds.equals(that.bounds)) return false;
        if (!equation.equals(that.equation)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = equation.hashCode();
        result = 31 * result + bounds.hashCode();
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }
}
