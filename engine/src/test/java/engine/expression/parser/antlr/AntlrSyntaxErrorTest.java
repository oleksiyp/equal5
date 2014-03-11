package engine.expression.parser.antlr;

import engine.expressions.parser.antlr.AntlrExpressionParser;
import engine.expression.parser.AbstractSyntaxErrorTest;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/23/13
 * Time: 10:32 PM
 */
public class AntlrSyntaxErrorTest extends AbstractSyntaxErrorTest<AntlrExpressionParser> {
    public AntlrSyntaxErrorTest() {
        super(new AntlrExpressionParser());
    }
}
