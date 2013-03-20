package engine.expressions;

import engine.calculation.Arguments;
import engine.calculation.functions.FunctionVisitor;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:34 PM
 */
public interface Function extends ParsableObject {
    void accept(FunctionVisitor visitor);

    double eval(Arguments arguments);
}
