package engine.expression.parser.antlr;

import engine.expressions.parser.antlr.AntlrAutocompletionParser;
import engine.expression.parser.AbstractAutocompletionParserTest;

import static org.junit.Assert.fail;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/27/13
 * Time: 1:23 PM
 */
public class AntlrAutocompletionParserTest extends AbstractAutocompletionParserTest<AntlrAutocompletionParser> {
    public AntlrAutocompletionParserTest() {
        super(new AntlrAutocompletionParser());
    }
}
