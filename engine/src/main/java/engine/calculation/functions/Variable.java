package engine.calculation.functions;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:47 PM
 */
public class Variable extends AbstractCalculable {
    private final String name;

    public Variable(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
    }

    public String getString() {
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

        return name.equals(variable.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
