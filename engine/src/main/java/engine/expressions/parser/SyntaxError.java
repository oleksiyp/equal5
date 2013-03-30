package engine.expressions.parser;

/**
* User: Oleksiy Pylypenko
* Date: 3/24/13
* Time: 9:47 PM
*/
public final class SyntaxError {
    private final boolean oneLiner;
    private final int line;
    private final int column;
    private final int startIndex;
    private final int endIndex;
    private final String message;

    public SyntaxError(int line, int column,
                       int startIndex, int endIndex,
                       String message) {
        this.oneLiner = false;
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
