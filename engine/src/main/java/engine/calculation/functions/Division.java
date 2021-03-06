package engine.calculation.functions;

import engine.expressions.Calculable;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:51 PM
 */
public class Division extends BinaryOperator {
    public Division(Calculable leftSide, Calculable rightSide) {
        super(leftSide, Type.DIVISION, rightSide);
    }

    @Override
    public void accept(FunctionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof Division && super.equals(o);

    }
}
