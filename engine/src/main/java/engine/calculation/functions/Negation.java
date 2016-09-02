package engine.calculation.functions;

import engine.expressions.Calculable;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/30/13
 * Time: 2:57 PM
 */
public class Negation extends AbstractCalculable {
    public static final int OPERATOR_PRIORITY = 0;

    private final Calculable operand;

    public Negation(Calculable operand) {
        this.operand = operand;
    }

    public Calculable getOperand() {
        return operand;
    }

    @Override
    public void accept(FunctionVisitor visitor) {
        visitor.visit(this);
    }
}
