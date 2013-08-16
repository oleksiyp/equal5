package engine.calculation.util;

import engine.calculation.functions.Addition;
import engine.calculation.functions.Constant;
import engine.calculation.functions.Multiplication;
import engine.expressions.Function;
import engine.expressions.parser.ClauseType;
import engine.expressions.parser.ParsingException;
import engine.expressions.parser.antlr.AntlrExpressionParser;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/10/13
 * Time: 12:16 AM
 */
public class ExpressionPrintingVisitorTest {

    private String printUsingVisitor(Function func) {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter(sWriter);
        ExpressionWriter eWriter = new ExpressionWriter(pWriter);
        ExpressionPrintingVisitor visitor = new ExpressionPrintingVisitor(eWriter);
        func.accept(visitor);
        pWriter.flush();
        return sWriter.toString();
    }

    //   +
    //  / \
    // 1  *
    //   / \
    //  2   3
    //
    // 1 + 2 * 3
    @Test
    public void testCase1() throws Exception {

        Addition expr = new Addition(new Constant(1),
                new Multiplication(new Constant(2), new Constant(3)));

        assertEquals("1.0+2.0*3.0", printUsingVisitor(expr));
    }

    //   *
    //  / \
    // 1  +
    //   / \
    //  2   3
    //
    // 1 * (2 + 3)
    @Test
    public void testCase2() throws Exception {
        Multiplication expr = new Multiplication(new Constant(1),
                new Addition(new Constant(2), new Constant(3)));

        assertEquals("1.0*(2.0+3.0)", printUsingVisitor(expr));
    }

    @Test
    public void testCase3() throws Exception {
        Function func = (Function) parse("(x+1)*(x+1)+(y+1)*(y+1)-25");

        assertEquals("(x+1.0)*(x+1.0)+(y+1.0)*(y+1.0)-25.0", printUsingVisitor(func));
    }

    private Object parse(String expression) throws ParsingException {
        return new AntlrExpressionParser().parse(ClauseType.ADDITIVE_EXPRESSION,
                expression);
    }
}
