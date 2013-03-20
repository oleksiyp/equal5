package engine.calculation;

import engine.locus.PixelDrawable;

/**
 * User: Oleksiy Pylypenko
 * At: 3/19/13  6:08 PM
 */
public class CalculationResults {
    private final CalculationParameters parameters;
    private final PixelDrawable[]drawables;

    public CalculationResults(CalculationParameters parameters, PixelDrawable[] drawables) {
        this.parameters = parameters;
        this.drawables = drawables;
    }

    public CalculationParameters getParameters() {
        return parameters;
    }

    public PixelDrawable[] getDrawables() {
        return drawables;
    }
}
