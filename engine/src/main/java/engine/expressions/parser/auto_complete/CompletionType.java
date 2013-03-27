package engine.expressions.parser.auto_complete;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/27/13
 * Time: 4:22 PM
 */
public enum CompletionType {
    FUNCTION_NAME,
    VARIABLE_NAME,
    EQUALITY_SIGN,
    FACTOR_OPERATOR, // *, /
    TERM_OPERATOR, // +, -
    CLOSE_BRACKET,
    OPEN_BRACKET,
    NUMBER
}
