package engine.calculation;

import engine.expressions.Function;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  3:18 PM
 */
public class ImmediateFunctionEvaluator implements FunctionEvaluator {
    public double calculate(Function function, Arguments arguments) {
        return new EvaluatingVisitor(arguments).calculate(function);
    }
}
