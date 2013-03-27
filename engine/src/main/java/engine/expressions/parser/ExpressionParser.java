package engine.expressions.parser;

import engine.expressions.Equation;
import engine.expressions.Function;
import engine.expressions.parser.ParsingException;
import engine.expressions.parser.auto_complete.AutocompletionParser;

/**
 * Syntax passed to this methods should be following:
 *
 * Number <- Constant | Variable
 * Parentheses <- '(' Expression ')'
 * Factor <- Number | Parentheses
 * Term <- Factor ( ('*' | '/') Factor ) *
 * Expression <- Term ( ('+' | '-') Term ) *
 * Equation <- Expression ( '=' | '<' | '>' ) Expression
 *
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 11:15 AM
 */
public interface ExpressionParser {
    Object parse(ClauseType clause,
                 String expression) throws ParsingException;

    AutocompletionParser createAutocompletionParser()
            throws UnsupportedOperationException;
}
