package engine.expressions.parser;

import engine.expressions.Function;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/30/13
 * Time: 6:03 PM
 */
public class SyntaxErrorMessages {

    public static String unknownVariable(String name) {
        return "Unknown variable or known constant '" + name + "'";
    }

    public static String unknownFunction(String name, Function[] args) {
        return "Unknown function '" + name
                + "' with " + args.length +
                " argument(s)";
    }

    public static String incorrectExpression() {
        return "Incorrect expression";
    }
}
