package engine.calculation;

import engine.calculation.drawables.Drawable;

/**
 * User: Oleksiy Pylypenko
 * At: 3/19/13  6:08 PM
 */
public class CalculationResults {
    private final CalculationParameters parameters;
    private final Drawable[] drawables;

    public CalculationResults(CalculationParameters parameters, Drawable[] drawables) {
        if (parameters == null) {
            throw new IllegalArgumentException("parameters");
        }
        if (drawables == null) {
            throw new IllegalArgumentException("drawables");
        }
        this.parameters = parameters;
        this.drawables = drawables;
    }

    public CalculationParameters getParameters() {
        return parameters;
    }

    public Drawable[] getDrawables() {
        return drawables;
    }
}
