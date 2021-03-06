package engine.expression.parser;

import engine.expressions.parser.ClauseType;
import engine.expressions.parser.ExpressionParser;
import engine.expressions.parser.ParsingException;
import engine.expressions.parser.SyntaxError;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.fail;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/23/13
 * Time: 10:32 PM
 */
public abstract class AbstractSyntaxErrorTest<EP extends ExpressionParser> {
    private EP parser;

    public AbstractSyntaxErrorTest(EP parser) {
        this.parser = parser;
    }

    public void showErrors(ClauseType clauseType,
                           String inputStr) {
        try {
            parser.parse(clauseType, inputStr);
            fail("Expression should not parse '" + inputStr + "', but did it");
        } catch (ParsingException e) {
            List<SyntaxError> errors = e.getErrors();
            System.out.println("|" + inputStr + "#|");
            printMarks(inputStr, errors);
            System.out.println("Please correct " + clauseType.toString().toLowerCase());
            printMessages(errors);
            System.out.println();
        }
    }

    private void printMessages(List<SyntaxError> errors) {
        int i = 1;
        for (SyntaxError error : errors) {
            System.out.println(i++ + ". " + error.getMessage());
        }
    }

    private void printMarks(String inputStr, List<SyntaxError> errors) {
        int []marks = new int[inputStr.length() + 1];
        for (SyntaxError error : errors) {
            int startIdx = error.getStartIndex();
            int endIndex = error.getEndIndex();
            for (int i = startIdx; i < endIndex; i++) {
                if (i < marks.length) {
                    marks[i]++;
                }
            }
        }
        System.out.print("|");
        for (int i = 0; i < marks.length; i++) {
            if (marks[i] == 1) {
                System.out.print('*');
            } else if (marks[i] > 1) {
                System.out.print(marks[i]);
            } else {
                System.out.print(' ');
            }
        }
        System.out.println("|");
    }

    @Test
    public void testExpressionsManually() throws Exception {
        showErrors(ClauseType.ADDITIVE_EXPRESSION, "1+");
        showErrors(ClauseType.ADDITIVE_EXPRESSION, "1*(5+");
        showErrors(ClauseType.ADDITIVE_EXPRESSION, "1-3*");
        showErrors(ClauseType.ADDITIVE_EXPRESSION, "abcdef(x)");
        showErrors(ClauseType.ADDITIVE_EXPRESSION, "sign(x,y)");
        showErrors(ClauseType.ADDITIVE_EXPRESSION, "(x,y)");
        showErrors(ClauseType.ADDITIVE_EXPRESSION, "(x-)");
        showErrors(ClauseType.ADDITIVE_EXPRESSION, "sin(x*)");
        showErrors(ClauseType.ADDITIVE_EXPRESSION, "(((5))");
    }

    @Test
    public void testEquationsManually() throws Exception {
        showErrors(ClauseType.EQUATIONS, "y= x=");
        showErrors(ClauseType.EQUATIONS, "y= x= y=");
        showErrors(ClauseType.EQUATIONS, "y= x= y= x=");
    }
}
