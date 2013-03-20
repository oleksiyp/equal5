package engine.calculation;

import engine.expressions.Equation;
import engine.locus.PixelDrawable;
import util.Cancelable;
import util.CancellationRoutine;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  3:13 PM
 */
public interface CalculationEngine extends Cancelable {
    void setSize(int width, int height);

    PixelDrawable []calculate(Equation ...equations);
}
