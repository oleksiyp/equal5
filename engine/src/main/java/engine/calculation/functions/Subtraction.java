package engine.calculation.functions;

import engine.expressions.Calculable;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:50 PM
 */
public class Subtraction extends BinaryOperator {
    public Subtraction(Calculable leftSide, Calculable rightSide) {
        super(leftSide, Type.SUBTRACTION, rightSide);
    }

    @Override
    public void accept(FunctionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof Subtraction && super.equals(o);

    }
}
