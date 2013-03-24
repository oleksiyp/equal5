package engine.expressions.parser;

import engine.expressions.parser.SyntaxError;

import java.util.List;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 11:17 AM
 */
public class ParsingException extends Exception {
    private final List<SyntaxError> errors;

    public ParsingException(List<SyntaxError> errors) {
        super(asMessage(errors));
        this.errors = errors;
    }

    private static String asMessage(List<SyntaxError> errors) {
        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for (SyntaxError err : errors) {
            if (!first) {
                builder.append("; ");
            }
            first = false;
            String message = err.getMessage();
            if (message == null) {
                message = "syntax error";
            }
            int count = err.getEndIndex() - err.getStartIndex();

            builder.append(count)
                    .append(" symbol(s) on ")
                    .append(err.isOneLiner() ? "" : "line " + err.getLine() + " and ")
                    .append("column ")
                    .append(err.getColumn())
                    .append(": ")
                    .append(message);
        }
        return builder.toString();
    }

    public List<SyntaxError> getErrors() {
        return errors;
    }
}
