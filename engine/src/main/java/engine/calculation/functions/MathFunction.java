package engine.calculation.functions;

import engine.expressions.Function;

/**
 * User: Oleksiy Pylypenko
 * At: 3/13/13  4:24 PM
 */
public class MathFunction extends AbstractFunction {
    public enum Type {
        SIN,
        COS,
        SIGNUM;

        public void accept(TypeVisitor visitor) {
            switch (this) {
                case SIN: visitor.sin(); return;
                case COS: visitor.cos(); return;
                case SIGNUM: visitor.signum(); return;
            }
        }
    }
    private final Type type;
    private final Function[] arguments;


    public MathFunction(Type type, Function... arguments) {
        this.type = type;
        this.arguments = arguments;
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

    public interface TypeVisitor {
        void sin();

        void cos();

        void signum();
    }

}
