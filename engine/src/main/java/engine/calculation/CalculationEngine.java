package engine.calculation;

import engine.expressions.Equation;
import engine.locus.PixelDrawable;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  3:13 PM
 */
public interface CalculationEngine {
    PixelDrawable calculate(
            int width, int height,
            Equation equation);
}
