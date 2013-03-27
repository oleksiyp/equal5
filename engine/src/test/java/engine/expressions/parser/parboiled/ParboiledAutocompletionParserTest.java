package engine.expressions.parser.parboiled;

import engine.expressions.parser.ClauseType;
import engine.expressions.parser.auto_complete.Completion;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/27/13
 * Time: 1:23 PM
 */
public class ParboiledAutocompletionParserTest {
    private ParboiledAutocompletionParser parser;
    private List<Completion> variants;
    private String expr;

    @Before
    public void setUp() throws Exception {
        parser = new ParboiledAutocompletionParser();
    }

    @Test
    public void testCompleteExpression() throws Exception {
        String str = "x * x+( pi/e+y ) *y<=pow(5,2)";

        for (int i = 0; i <= str.length(); i++)
        {
            String expr = str.substring(0, i);
            System.out.println();
            System.out.println("Expression: '" + expr + "'");
            autoComplete(expr);
            System.out.println(variants);
        }

//        testContainsCompletion("+", "-", "*", "/");
//        testHaveFunctionNameCompletion("x");
    }

    private void testCompletion(Completion ...completion) {
        List<Completion> badCompletions = new ArrayList<Completion>();
        badCompletions.addAll(variants);
        badCompletions.removeAll(Arrays.asList(completion));
        if (!badCompletions.isEmpty()) {
            fail("Function name completion " + badCompletions + " not matched '" +
                    expr + "'");
        }
    }

    private void autoComplete(String expr) {
        this.expr = expr;
        variants = parser.completeExpression(ClauseType.EQUATIONS, expr);
    }
}
