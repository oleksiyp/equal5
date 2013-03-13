package engine.expressions;

import engine.calculation.FunctionVisitor;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:34 PM
 */
public interface Function {
    void accept(FunctionVisitor visitor);
}
