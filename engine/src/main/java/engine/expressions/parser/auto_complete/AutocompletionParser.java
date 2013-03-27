package engine.expressions.parser.auto_complete;

import engine.expressions.parser.ClauseType;

import java.util.List;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/27/13
 * Time: 12:32 PM
 */
public interface AutocompletionParser {
    List<Completion> completeExpression(ClauseType clause,
                                        String expressionStart);
}
