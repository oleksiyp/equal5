package engine.calculation.functions;

import engine.calculation.Arguments;
import engine.expressions.Function;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/30/13
 * Time: 2:57 PM
 */
public class Negation extends AbstractFunction {
    public static final int OPERATOR_PRIORITY = 0;

    private final Function operand;

    public Negation(Function operand) {
        this.operand = operand;
    }

    public Function getOperand() {
        return operand;
    }

    @Override
    public void accept(FunctionVisitor visitor) {
        visitor.visit(this);
    }
}
