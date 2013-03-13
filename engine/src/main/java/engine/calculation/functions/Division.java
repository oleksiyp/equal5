package engine.calculation.functions;

import engine.expressions.Function;
import engine.calculation.FunctionVisitor;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:51 PM
 */
public class Division extends BinaryOperator {
    public Division(Function leftSide, Function rightSide) {
        super(leftSide, Type.DIVISION, rightSide);
    }

    @Override
    public void accept(FunctionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Division)) return false;

        return super.equals(o);
    }
}
