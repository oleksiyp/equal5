package engine.expressions.parser;

import engine.expressions.parser.parboiled.ParboiledExpressionParser;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/23/13
 * Time: 10:32 PM
 */
public class SyntexErrorTest {
    private ParboiledExpressionParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new ParboiledExpressionParser();
    }

    @Test
    public void testParse() throws Exception {
        System.out.println(Arrays.toString((Object[])parser.parse(ClauseType.EQUATIONS,
                "y = sin(x) * (5+x*3-4*5/3/2) y=x")));
    }
}
