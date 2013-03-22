package engine.calculation.functions;

import engine.expressions.Function;

import java.util.Arrays;

/**
 * User: Oleksiy Pylypenko
 * At: 3/13/13  4:24 PM
 */
public class MathFunction extends AbstractFunction {
    public enum Type {
        SIN("sin", 1),
        COS("cos", 1),
        SIGNUM("sign", 1);
        private final String inExpressionName;
        private final int argumentsCount;

        private Type(String inExpressionName, int argumentsCount) {
            this.inExpressionName = inExpressionName;
            this.argumentsCount = argumentsCount;
        }

        public void accept(TypeVisitor visitor) {
            switch (this) {
                case SIN: visitor.sin(); return;
                case COS: visitor.cos(); return;
                case SIGNUM: visitor.signum(); return;
            }
        }

        public String getInExpressionName() {
            return inExpressionName;
        }

        public int getArgumentsCount() {
            return argumentsCount;
        }
    }
    private final Type type;
    private final Function[] arguments;


    public MathFunction(Type type, Function... arguments) {
        this.type = type;
        this.arguments = arguments;
        if (type.getArgumentsCount() != arguments.length) {
            throw new IllegalArgumentException("arguments");
        }
    }

    public Function[] getArguments() {
        return arguments;
    }

    public Type getType() {
        return type;
    }

    @Override
    public void accept(FunctionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MathFunction)) return false;

        MathFunction that = (MathFunction) o;

        if (!Arrays.equals(arguments, that.arguments)) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + Arrays.hashCode(arguments);
        return result;
    }

    public interface TypeVisitor {
        void sin();

        void cos();

        void signum();
    }

}
