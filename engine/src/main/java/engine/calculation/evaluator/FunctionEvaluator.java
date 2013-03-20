package engine.calculation.evaluator;

import engine.calculation.Arguments;
import engine.expressions.Function;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  3:31 PM
 */
public interface FunctionEvaluator {
    double calculate(Function function, Arguments arguments);
}
