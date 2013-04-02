package engine.expression.parser;

import engine.expressions.parser.ClauseType;
import engine.expressions.parser.auto_complete.AutocompletionParser;
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
public abstract class AbstractAutocompletionParserTest<AP extends AutocompletionParser> {
    private final AP parser;
    private List<Completion> variants;
    private String expr;

    public AbstractAutocompletionParserTest(AP parser) {
        this.parser = parser;
    }

    @Test
    public void testCompletionsManually() throws Exception {
        printCompletions("x * x+( pi/e+y ) *y<=pow(5,2)");
    }

    private void printCompletions(String str) {
        System.out.println("Completions for expression '" + str + "':");
        for (int i = 0; i <= str.length(); i++)
        {
            String expr = str.substring(0, i);
            System.out.println("\"" + expr + "\"");
            autoComplete(expr);
            printVariants();
        }
    }

    private void printVariants() {
        for (int i = 0; i < variants.size(); i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print(variants.get(i));
        }
        System.out.println();
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
