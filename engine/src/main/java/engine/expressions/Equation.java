package engine.expressions;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:32 PM
 */
public class Equation {
    public interface TypeVisitor<T> {
        T less();

        T equal();

        T greater();

        T lessEqual();

        T greaterEqual();
    }
    public enum Type {
        LESS, EQUAL, GREATER,
        LESS_EQUAL, GREATER_EQUAL;

        public <T> T accept(TypeVisitor<T> visitor) {
            switch (this) {
                case LESS: return visitor.less();
                case EQUAL: return visitor.equal();
                case GREATER: return visitor.greater();
                case LESS_EQUAL: return visitor.lessEqual();
                case GREATER_EQUAL: return visitor.greaterEqual();
            }
            throw new IllegalStateException("bad Equation.Type");
        }
    }

    private final Type type;

    private final Function leftPart, rightPart;

    public Equation(Function leftPart, Type type, Function rightPart) {
        this.leftPart = leftPart;
        this.type = type;
        this.rightPart = rightPart;
    }


    public Type getType() {
        return type;
    }

    public Function getLeftPart() {
        return leftPart;
    }

    public Function getRightPart() {
        return rightPart;
    }
}
