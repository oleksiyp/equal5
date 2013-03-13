package engine.calculation.functions;

import engine.calculation.AbstractFunction;
import engine.calculation.FunctionVisitor;
import engine.expressions.Function;

/**
 * User: Oleksiy Pylypenko
 * At: 3/13/13  4:24 PM
 */
public class MathFunction extends AbstractFunction {
    public interface TypeVisitor {
        void sin();

        void cos();
    }

    public enum Type {
        SIN,
        COS;

        public void accept(TypeVisitor visitor) {
            switch (this) {
                case SIN: visitor.sin(); return;
                case COS: visitor.cos(); return;
            }
        }
    }

    private final Type type;
    private final Function[] arguments;


    public MathFunction(Type type, Function[] arguments) {
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
}
