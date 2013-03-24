package engine.expressions.parser;

import engine.expressions.Equation;
import engine.expressions.Function;
import engine.expressions.parser.ParsingException;

/**
 * Syntax passed to this methods should be following:
 *
 * Number <- Constant | Variable
 * Parents <- '(' Expression ')'
 * Factor <- Number | Parents
 * Term <- Factor ( ('*' | '/') Factor ) *
 * Expression <- Term ( ('+' | '-') Term ) *
 * Equation <- Expression ( '=' | '<' | '>' ) Expression
 *
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 11:15 AM
 */
public interface ExpressionParser {
    Function parseExpression(String expression)
            throws ParsingException;

    Equation[] parseEquations(String expression)
            throws ParsingException;
}
