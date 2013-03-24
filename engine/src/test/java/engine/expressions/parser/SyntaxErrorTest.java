package engine.expressions.parser;

import engine.expressions.parser.parboiled.ParboiledExpressionParser;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.fail;

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

    public void showErrors(ClauseType clauseType,
                           String inputStr) {
        try {
            parser.parse(clauseType, inputStr);
            fail("Expression should not parse '" + inputStr + "', but did it");
        } catch (ParsingException e) {
            List<SyntaxError> errors = e.getErrors();
            System.out.println(inputStr);
            printMarks(inputStr, errors);
            printMessages(errors);
        }
    }

    private void printMessages(List<SyntaxError> errors) {
        int i = 1;
        for (SyntaxError error : errors) {
            System.out.println(i++ + ". " + error.getMessage());
        }
    }

    private void printMarks(String inputStr, List<SyntaxError> errors) {
        boolean []marks = new boolean[inputStr.length() + 1];
        for (SyntaxError error : errors) {
            int startIdx = error.getStartIndex();
            int endIndex = error.getEndIndex();
            for (int i = startIdx; i < endIndex; i++) {
                if (i < marks.length) {
                    marks[i] = true;
                }
            }
        }
        for (int i = 0; i < marks.length; i++) {
            System.out.print(marks[i] ? '*' : ' ');
        }
        System.out.println();
    }

    @Test
    public void testParse() throws Exception {
        showErrors(ClauseType.EXPRESSION, "1+");
    }
}
