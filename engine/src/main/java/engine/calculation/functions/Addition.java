package engine.calculation.functions;

import engine.expressions.Function;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:49 PM
 */
public class Addition extends BinaryOperator {
    public Addition(Function leftSide, Function rightSide) {
        super(leftSide, Type.ADDITION, rightSide);
    }

    @Override
    public void accept(FunctionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof Addition && super.equals(o);

    }

    public static Addition sequence(Function ...addends) {
        if (addends.length < 2) {
            throw new IllegalArgumentException("addends");
        }
        Addition ret = new Addition(addends[0],
                addends[1]);
        for (int i = 2; i < addends.length; i++) {
            ret = new Addition(ret, addends[i]);
        }
        return ret;
    }
}
