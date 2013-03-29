package engine.expressions.parser.parboiled;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/24/13
 * Time: 2:13 AM
 */
class ParsingFailureException extends Throwable {
    private final TreeNodeError error;

    public ParsingFailureException(TreeNodeError error) {
        super(error.getErrorMessage());
        this.error = error;
    }

    public TreeNodeError getError() {
        return error;
    }
}
