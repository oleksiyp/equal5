package engine.expressions.parser;

import engine.expressions.ClauseType;
import org.junit.Before;
import org.junit.Test;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/23/13
 * Time: 10:32 PM
 */
public class SyntaxErrorTest {
    private ParboiledExpressionParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new ParboiledExpressionParser();
    }

    @Test
    public void testParse() throws Exception {
        System.out.println(parser.parse(ClauseType.EQUATION,
                "y=1+x+sin(1)+(2+3*4)"));
    }
}
