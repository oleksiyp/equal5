package engine.expressions.parser;

import java.util.List;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 11:17 AM
 */
public class ParsingException extends Exception {
    private String expression;
    private final List<SyntaxError> errors;

    public ParsingException(List<SyntaxError> errors) {
        super(asMessage(errors));
        this.errors = errors;
        this.expression = null;
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

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage();
        if (expression != null) {
            msg += "\n";
            msg += "Expr: " + expression;
        }
        return msg;
    }
}
