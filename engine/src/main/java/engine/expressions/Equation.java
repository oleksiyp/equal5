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
        LESS("<"), EQUAL("="), GREATER(">"),
        LESS_EQUAL("<="), GREATER_EQUAL(">=");

        private final String operator;

        Type(String operator) {
            this.operator = operator;
        }

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

        public static Type byOperator(String str) {
            for (Type t : Type.values()) {
                if (t.operator.equals(str)) {
                    return t;
                }
            }
            return null;
        }
    }

    private final Type type;

    private final Function leftPart, rightPart;

    public Equation(Function leftPart, Type type, Function rightPart) {
        if (leftPart == null) {
            throw new IllegalArgumentException("leftPart");
        }
        if (type == null) {
            throw new IllegalArgumentException("leftPart");
        }
        if (rightPart == null) {
            throw new IllegalArgumentException("leftPart");
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equation)) return false;

        Equation equation = (Equation) o;

        if (!leftPart.equals(equation.leftPart)) return false;
        if (!rightPart.equals(equation.rightPart)) return false;
        if (type != equation.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + leftPart.hashCode();
        result = 31 * result + rightPart.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return leftPart + type.operator + rightPart;
    }
}
