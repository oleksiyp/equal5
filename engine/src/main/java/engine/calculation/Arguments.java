package engine.calculation;


/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:38 PM
 */
public interface Arguments {
    Arguments EMPTY = new EmptyArguments();

    String [] getArgumentNames();

    double getValue(String name);
}


class EmptyArguments implements Arguments {
    @Override
    public String[] getArgumentNames() {
        return new String[0];
    }

    @Override
    public double getValue(String name) {
        throw new UnknownArgumentUsedException(name);
    }
}