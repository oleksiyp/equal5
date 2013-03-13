package engine.expressions;

import java.util.List;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 11:17 AM
 */
public class ParsingException extends Exception {
    public final static class SyntaxError {
        private final boolean oneLiner;
        private final int line;
        private final int column;
        private final int startIndex;
        private final int endIndex;
        private final String message;

        public SyntaxError(boolean oneLiner, int line, int column,
                           int startIndex, int endIndex,
                           String message) {
            this.oneLiner = oneLiner;
            this.line = line;
            this.column = column;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.message = message;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public String getMessage() {
            return message;
        }

        public boolean isOneLiner() {
            return oneLiner;
        }
    }

    public ParsingException(List<SyntaxError> errors) {
        super(asMessage(errors));
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
}
