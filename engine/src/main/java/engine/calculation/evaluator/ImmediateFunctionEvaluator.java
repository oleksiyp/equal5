package engine.calculation.evaluator;

import engine.calculation.Arguments;
import engine.expressions.Calculable;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  3:18 PM
 */
public class ImmediateFunctionEvaluator implements FunctionEvaluator {
    public double calculate(Calculable calculable, Arguments arguments) {
        return new EvaluatingVisitor(arguments).calculate(calculable);
    }
}
