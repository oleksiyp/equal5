package engine.calculation;


/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:43 PM
 */
public class UnknownArgumentUsedException extends RuntimeException {
    private String name;

    public UnknownArgumentUsedException(String name) {
        super("unknown argument used: " + name);
        this.name = name;
    }
}
