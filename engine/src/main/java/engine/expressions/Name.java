package engine.expressions;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:41 PM
 */
public class Name {
    private final String symbols;

    public Name(String symbols) {
        if (symbols == null) {
            throw new NullPointerException("name");
        }
        this.symbols = symbols;
    }


    public String getSymbols() {
        return symbols;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Name name = (Name) o;

        if (!this.symbols.equals(name.symbols)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return symbols.hashCode();
    }
}
