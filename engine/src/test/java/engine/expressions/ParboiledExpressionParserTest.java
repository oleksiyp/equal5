package engine.expressions;

import static junit.framework.Assert.*;

import engine.calculation.functions.*;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 12:49 PM
 */
public class ParboiledExpressionParserTest {
    private ParboiledExpressionParser parser;
    private Constant one;
    private Constant two;
    private Constant three;
    private Constant five;
    private Variable xVar;
    private Variable aVar;

    @Before
    public void setUp() throws Exception {
        parser = new ParboiledExpressionParser();
        one = new Constant(1);
        two = new Constant(2);
        three = new Constant(3);
        five = new Constant(5);
        xVar = new Variable(new Name("x"));
        aVar = new Variable(new Name("a"));
    }

    @Test
    public void testParseFunction1() throws Exception {
        Function parsed = parser.parseFunction("2*(1+x+a+3)");

        Function inParents = Addition.sequence(one, xVar, aVar, three);
        Multiplication expected = new Multiplication(two, inParents);
        assertEquals(expected, parsed);
    }

    @Test
    public void testParseFunction2() throws Exception {
        Function parsed = parser.parseFunction("2*(1+x+a+3-5)");

        Function inParents = Addition.sequence(one, xVar, aVar, three);
        inParents = new Subtraction(inParents, five);
        Multiplication expected = new Multiplication(two, inParents);

        assertEquals(expected, parsed);
    }

    @Test(expected = ParsingException.class)
    public void testParseFunctionFail1() throws Exception {
        parseFunction("(");
    }

    @Test(expected = ParsingException.class)
    public void testParseFunctionFail2() throws Exception {
        parseFunction("1*");
    }

    @Test(expected = ParsingException.class)
    public void testParseFunctionFail3() throws Exception {
        parseFunction("()");
    }

    @Test(expected = ParsingException.class)
    public void testParseFunctionFail4() throws Exception {
        parseFunction("*1");
    }

    // DIGIT

    @Test
    public void testDigit() throws Exception {
        // no exception here
        for (int i = 0; i <= 9; i++) {
            parse(ClauseType.DIGIT,
                    "" + i);
        }
    }



    @Test(expected = ParsingException.class)
    public void testDigitFail1() throws Exception {
        parse(ClauseType.DIGIT, "a");
    }

    @Test(expected = ParsingException.class)
    public void testDigitFail2() throws Exception {
        parse(ClauseType.DIGIT, "0a");
    }

    @Test(expected = ParsingException.class)
    public void testDigitFail3() throws Exception {
        parse(ClauseType.DIGIT, "aa");
    }

    private Object parse(ClauseType type, String string) throws ParsingException {
        return new ParboiledExpressionParser().parse(type, string, true);
    }

    private void parseFunction(String expr) throws ParsingException {
        new ParboiledExpressionParser().parseFunction(expr);
    }

    // EXPONENT


    @Test
    public void testExponent() throws Exception {
        parse(ClauseType.EXPONENT, "e1");
        parse(ClauseType.EXPONENT, "e10");
        parse(ClauseType.EXPONENT, "e+10");
        parse(ClauseType.EXPONENT, "e-10");
        parse(ClauseType.EXPONENT, "E12");
        parse(ClauseType.EXPONENT, "E-12");
    }

    @Test(expected = ParsingException.class)
    public void testExponentFail1() throws Exception {
        parse(ClauseType.EXPONENT, "e");
    }
    @Test(expected = ParsingException.class)
    public void testExponentFail2() throws Exception {
        parse(ClauseType.EXPONENT, "10");
    }
    @Test(expected = ParsingException.class)
    public void testExponentFail3() throws Exception {
        parse(ClauseType.EXPONENT, "-10");
    }
    @Test(expected = ParsingException.class)
    public void testExponentFail4() throws Exception {
        parse(ClauseType.EXPONENT, "+10");
    }

    // DECIMAL_FLOAT
    @Test
    public void testDecimalFloat() throws Exception {
        parse(ClauseType.DECIMAL_FLOAT, "1");
        parse(ClauseType.DECIMAL_FLOAT, "10");
        parse(ClauseType.DECIMAL_FLOAT, "10.5");
        parse(ClauseType.DECIMAL_FLOAT, "10.5e1");
        parse(ClauseType.DECIMAL_FLOAT, "10.e1");
        parse(ClauseType.DECIMAL_FLOAT, "10.5e-1");
        parse(ClauseType.DECIMAL_FLOAT, "10.e-1");
        parse(ClauseType.DECIMAL_FLOAT, ".5");
        parse(ClauseType.DECIMAL_FLOAT, ".5e-1");
        parse(ClauseType.DECIMAL_FLOAT, "1e1");
        parse(ClauseType.DECIMAL_FLOAT, "1e-1");
        parse(ClauseType.DECIMAL_FLOAT, "1e+1");
    }

    @Test(expected = ParsingException.class)
    public void testDecimalFloatFail1() throws Exception {
        parse(ClauseType.DECIMAL_FLOAT, "e");
    }
    @Test(expected = ParsingException.class)
    public void testDecimalFloatFail2() throws Exception {
        parse(ClauseType.DECIMAL_FLOAT, ".");
    }

    // VARIABLE
    @Test
    public void testVariable() throws Exception {
        parse(ClauseType.VARIABLE, "a");
        parse(ClauseType.VARIABLE, "b");
        parse(ClauseType.VARIABLE, "aa");
        parse(ClauseType.VARIABLE, "bb");
        parse(ClauseType.VARIABLE, "cc");
        parse(ClauseType.VARIABLE, "abcdefghiklmnopqrstuvwxyz");
    }

    @Test(expected = ParsingException.class)
    public void testVariableFail1() throws Exception {
        parse(ClauseType.VARIABLE, "123");
    }
    @Test(expected = ParsingException.class)
    public void testVariableFail2() throws Exception {
        parse(ClauseType.VARIABLE, ",");
    }
    @Test(expected = ParsingException.class)
    public void testVariableFail3() throws Exception {
        parse(ClauseType.VARIABLE, " ");
    }
    @Test(expected = ParsingException.class)
    public void testVariableFail4() throws Exception {
        parse(ClauseType.VARIABLE, "");
    }
    @Test(expected = ParsingException.class)
    public void testVariableFail5() throws Exception {
        parse(ClauseType.VARIABLE, "a0");
    }

    @Test
    public void testConstant() throws Exception {
        Assert.assertEquals(123.0, parseConstant("123").getValue(), 1e-6);
        Assert.assertEquals(1e6, parseConstant("1e6").getValue(), 1e-6);
        Assert.assertEquals(1e6, parseConstant("1e+6").getValue(), 1e-6);
        Assert.assertEquals(1e-6, parseConstant("0.1e-5").getValue(), 1e-6);
        Assert.assertEquals(25.25, parseConstant("2525e-2").getValue(), 1e-6);
    }

    private Constant parseConstant(String string) throws ParsingException {
        return (Constant) parse(ClauseType.CONSTANT, string);
    }

    @Test
    public void testParents() throws Exception {
        Object obj = parse(ClauseType.PARENTS, "(123)");

    }
}
