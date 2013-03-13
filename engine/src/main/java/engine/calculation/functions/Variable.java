package engine.calculation.functions;

import engine.calculation.AbstractFunction;
import engine.calculation.FunctionVisitor;
import engine.expressions.Name;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:47 PM
 */
public class Variable extends AbstractFunction {
    private final Name name;

    public Variable(Name name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
    }

    public Name getName() {
        return name;
    }

    @Override
    public void accept(FunctionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variable)) return false;

        Variable variable = (Variable) o;

        if (!name.equals(variable.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
