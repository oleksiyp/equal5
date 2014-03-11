package engine.expression.parser.antlr;

import engine.expressions.parser.antlr.AntlrExpressionParser;
import engine.expression.parser.AbstractExpressionParserTest;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 12:49 PM
 */
public class AntlrExpressionParserTest extends AbstractExpressionParserTest<AntlrExpressionParser> {
    public AntlrExpressionParserTest() {
        super(new AntlrExpressionParser());
    }
}
