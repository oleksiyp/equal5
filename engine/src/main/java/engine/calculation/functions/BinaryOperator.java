package engine.calculation.functions;

import engine.calculation.AbstractFunction;
import engine.expressions.Function;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:49 PM
 */
public abstract class BinaryOperator extends AbstractFunction {
    protected final Type type;
    protected final Function leftSide, rightSide;

    protected BinaryOperator(Function leftSide,
                             Type type,
                             Function rightSide) {
        this.type = type;
        if (leftSide == null) {
            throw new NullPointerException("leftSide");
        }
        if (rightSide == null) {
            throw new NullPointerException("rightSide");
        }
        if (type == null) {
            throw new NullPointerException("operator");
        }
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    public Function getLeftSide() {
        return leftSide;
    }

    public Function getRightSide() {
        return rightSide;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BinaryOperator)) return false;

        BinaryOperator that = (BinaryOperator) o;

        if (!leftSide.equals(that.leftSide)) return false;
        if (type != that.type) return false;
        if (!rightSide.equals(that.rightSide)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + leftSide.hashCode();
        result = 31 * result + rightSide.hashCode();
        return result;
    }

    public int getPriority() {
        if (type == Type.OTHER) {
            throw new UnsupportedOperationException("getPriority");
        }
        return type.priority;
    }

    public String getSymbolicRepresentation() {
        if (type == Type.OTHER) {
            throw new UnsupportedOperationException("getSymbolicRepresentation");
        }
        return type.symbolicRepresentation;
    }

    /**
     * User: Oleksiy Pylypenko
     * Date: 2/9/13
     * Time: 11:58 PM
     */
    protected static enum Type {
        ADDITION("+", 0), SUBTRACTION("-", 0),
        MULTIPLICATION("*", 1), DIVISION("/", 1),
        POWER("^", 2),
        OTHER("", -1);

        private final String symbolicRepresentation;
        private final int priority;

        private Type(String symbolicRepresentation, int priority) {
            this.symbolicRepresentation = symbolicRepresentation;
            this.priority = priority;
        }
    }
}
