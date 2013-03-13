package engine.calculation.functions;

import engine.expressions.Function;
import engine.calculation.FunctionVisitor;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:50 PM
 */
public class Subtraction extends BinaryOperator {
    public Subtraction(Function leftSide, Function rightSide) {
        super(leftSide, Type.SUBTRACTION, rightSide);
    }

    @Override
    public void accept(FunctionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtraction)) return false;

        return super.equals(o);
    }
}
