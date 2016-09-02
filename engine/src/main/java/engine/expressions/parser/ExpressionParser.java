package engine.expressions.parser;

import engine.expressions.parser.auto_complete.AutocompletionParser;

import java.util.List;
import java.util.Map;

/**
 * Syntax passed to this methods should be following:
 *
 *
 * Number &lt;- Constant | Variable
 * Parentheses &lt;- '(' Expression ')'
 * Factor &lt;- Number | Parentheses
 * Term &lt;- Factor ( ('*' | '/') Factor ) *
 * Expression &lt;- Term ( ('+' | '-') Term ) *
 * Equation &lt;- Expression ( '=' | '&lt;' | '&gt;' ) Expression
 *
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 11:15 AM
 */
public interface ExpressionParser {
    Object parse(ClauseType clause,
                 String expression) throws ParsingException;

    AutocompletionParser createAutocompletionParser();

    void setKnownConstants(Map<String, Double> knownConstants);

    void setVarList(List<String> varList);

    Map<String, Double> getKnownConstants();

    List<String> getVarList();
}
