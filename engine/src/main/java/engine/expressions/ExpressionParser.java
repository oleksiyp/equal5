package engine.expressions;

/**
 * Syntax passed to this methods should be following:
 *
 * Number <- Constant | Variable
 * Parents <- '(' Function ')'
 * Factor <- Number | Parents
 * Term <- Factor ( ('*' | '/') Factor ) *
 * Function <- Term ( ('+' | '-') Term ) *
 * Equation <- Function ( '=' | '<' | '>' ) Function
 *
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 11:15 AM
 */
public interface ExpressionParser {
    Function parseFunction(String expression)
            throws ParsingException;

    Equation parseEquation(String expression)
            throws ParsingException;
}
