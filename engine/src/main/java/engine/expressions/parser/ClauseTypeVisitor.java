package engine.expressions.parser;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/30/13
 * Time: 12:10 PM
 */
public interface ClauseTypeVisitor<T> {
    T equations() throws Exception;

    T equation() throws Exception;

    T additiveExpression() throws Exception;

    T primaryExpression() throws Exception;

    T multiplicativeExpression() throws Exception;

    T parentheses() throws Exception;

    T constant() throws Exception;

    T variable() throws Exception;

    T arguments() throws Exception;

    T mathFunction() throws Exception;
}
