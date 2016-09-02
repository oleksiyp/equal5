package engine.calculation.functions;

import engine.expressions.Calculable;

import java.util.Arrays;

/**
 * User: Oleksiy Pylypenko
 * At: 3/13/13  4:24 PM
 */
public class MathCalculable extends AbstractCalculable {
    private final MathFunctionType type;
    private final Calculable[] arguments;

    public MathCalculable(MathFunctionType type, Calculable... arguments) {
        if (type == null) {
            throw new IllegalArgumentException("type");
        }
        if (arguments == null) {
            throw new IllegalArgumentException("arguments");
        }

        this.type = type;
        this.arguments = arguments;
        if (type.getArgumentsCount() != arguments.length) {
            throw new IllegalArgumentException("arguments");
        }
    }

    public Calculable[] getArguments() {
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
        if (!(o instanceof MathCalculable)) return false;

        MathCalculable that = (MathCalculable) o;

        if (type.isWithSideEffects()) return false;
        if (that.type.isWithSideEffects()) return false;

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
