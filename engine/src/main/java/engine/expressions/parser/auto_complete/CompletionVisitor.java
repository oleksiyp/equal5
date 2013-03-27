package engine.expressions.parser.auto_complete;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/27/13
 * Time: 7:18 PM
 */
public interface CompletionVisitor {
    void functionName(String prefix);

    void variableName(String prefix);

    void equalitySign(String prefix);

    void factorOperator();

    void termOperator();

    void closeBracket();

    void openBracket();

    void number(String prefix);
}
