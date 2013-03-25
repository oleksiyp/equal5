package engine.calculation.functions;

import engine.expressions.Function;

import java.util.Arrays;

/**
 * User: Oleksiy Pylypenko
 * At: 3/13/13  4:24 PM
 */
public class MathFunction extends AbstractFunction {
    private final MathFunctionType type;
    private final Function[] arguments;

    public MathFunction(MathFunctionType type, Function... arguments) {
        this.type = type;
        this.arguments = arguments;
        if (type.getArgumentsCount() != arguments.length) {
            throw new IllegalArgumentException("arguments");
        }
    }

    public Function[] getArguments() {
        return arguments;
    }

    public MathFunctionType getType() {
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

        if (type == MathFunctionType.RANDOM) return false;
        if (that.type == MathFunctionType.RANDOM) return false;

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

}
