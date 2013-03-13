package engine.calculation;

import engine.expressions.Name;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:43 PM
 */
public class UnknownArgumentUsedException extends RuntimeException {
    private Name name;

    public UnknownArgumentUsedException(Name name) {
        super("unknown argument used: " + name.getSymbols());
        this.name = name;
    }
}
