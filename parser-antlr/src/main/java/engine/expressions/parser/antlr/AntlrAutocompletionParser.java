package engine.expressions.parser.antlr;

import engine.expressions.parser.ClauseType;
import engine.expressions.parser.auto_complete.AutocompletionParser;
import engine.expressions.parser.auto_complete.Completion;

import java.util.List;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/30/13
 * Time: 10:40 AM
 */
public class AntlrAutocompletionParser implements AutocompletionParser {
    @Override
    public List<Completion> completeExpression(ClauseType clause, String expressionStart) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
