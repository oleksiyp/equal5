package engine.calculation;

import engine.calculation.tasks.CalculationParameters;
import engine.calculation.tasks.CalculationResults;
import engine.calculation.tasks.ViewportBounds;
import engine.expressions.Equation;
import engine.locus.PixelDrawable;
import util.Cancelable;
import util.CancellationRoutine;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  3:13 PM
 */
public interface CalculationEngine {
    CalculationResults calculate(CalculationParameters parameters);
}
