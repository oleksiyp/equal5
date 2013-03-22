package engine.expressions;

import static junit.framework.Assert.*;

import engine.calculation.Arguments;
import engine.calculation.functions.*;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 12:49 PM
 */
public class ParboiledExpressionParserTest {
    public static final double EPS = 1e-6;
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
        xVar = new Variable("x");
        aVar = new Variable("a");
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
        Assert.assertEquals(123.0, parseConstant("123").getValue(), EPS);
        Assert.assertEquals(1e6, parseConstant("1e6").getValue(), EPS);
        Assert.assertEquals(1e6, parseConstant("1e+6").getValue(), EPS);
        Assert.assertEquals(EPS, parseConstant("0.1e-5").getValue(), EPS);
        Assert.assertEquals(25.25, parseConstant("2525e-2").getValue(), EPS);
    }

    @Test(expected = ParsingException.class)
    public void testConstantFail1() throws Exception {
        parseConstant("1..1");
    }
    @Test(expected = ParsingException.class)
    public void testConstantFail2() throws Exception {
        parseConstant("1ee1");
    }
    @Test(expected = ParsingException.class)
    public void testConstantFail3() throws Exception {
        parseConstant("1e.e2");
    }
    @Test(expected = ParsingException.class)
    public void testConstantFail4() throws Exception {
        parseConstant("1e1e");
    }
    @Test(expected = ParsingException.class)
    public void testConstantFail5() throws Exception {
        parseConstant("1e.");
    }
    @Test(expected = ParsingException.class)
    public void testConstantFail6() throws Exception {
        parseConstant(".1e.1");
    }
    @Test(expected = ParsingException.class)
    public void testConstantFail7() throws Exception {
        parseConstant("1e.1");
    }

    private Constant parseConstant(String string) throws ParsingException {
        return (Constant) parse(ClauseType.CONSTANT, string);
    }

    @Test
    public void testParents() throws Exception {
        Assert.assertEquals(123, evalExpr(ClauseType.PARENTS, "(123)"), EPS);
        Assert.assertEquals(123, evalExpr(ClauseType.PARENTS, "((123))"), EPS);
        Assert.assertEquals(123, evalExpr(ClauseType.PARENTS, "(((123)))"), EPS);
        Assert.assertEquals(11, evalExpr(ClauseType.PARENTS, "(((5+6)))"), EPS);
        Assert.assertEquals(76, evalExpr(ClauseType.PARENTS, "(((5+6)+(3+2)*(8+5)))"), EPS);
    }

    @Test(expected = ParsingException.class)
    public void testParentsFail1() throws Exception {
        evalExpr(ClauseType.PARENTS, "(");
    }
    @Test(expected = ParsingException.class)
    public void testParentsFail2() throws Exception {
        evalExpr(ClauseType.PARENTS, ")");
    }
    @Test(expected = ParsingException.class)
    public void testParentsFail3() throws Exception {
        evalExpr(ClauseType.PARENTS, ")(");
    }
    @Test(expected = ParsingException.class)
    public void testParentsFail4() throws Exception {
        evalExpr(ClauseType.PARENTS, "(()");
    }
    @Test(expected = ParsingException.class)
    public void testParentsFail5() throws Exception {
        evalExpr(ClauseType.PARENTS, "((5)(5))");
    }

    private double evalExpr(ClauseType type, String expr) throws ParsingException {
        return ((Function) parse(type, expr)).eval(Arguments.EMPTY);
    }

    @Test
    public void testFactor() throws Exception {
        Assert.assertEquals(2, evalExpr(ClauseType.FACTOR, "2"), EPS);
        Assert.assertEquals(2, evalExpr(ClauseType.FACTOR, "(2)"), EPS);
        Assert.assertEquals(1.0, evalExpr(ClauseType.FACTOR, "sign(5)"), EPS);
    }

    @Test(expected = ParsingException.class)
    public void testFactorFail1() throws Exception {
        evalExpr(ClauseType.FACTOR, "1+1");
    }
    @Test(expected = ParsingException.class)
    public void testFactorFail2() throws Exception {
        evalExpr(ClauseType.FACTOR, "2-1");
    }

    @Test
    public void testTerm() throws Exception {
        Assert.assertEquals(6, evalExpr(ClauseType.TERM, "2*3"), EPS);
        Assert.assertEquals(60, evalExpr(ClauseType.TERM, "2*5*6"), EPS);
        Assert.assertEquals(36*14, evalExpr(ClauseType.TERM, "2*3*6*(6+8)"), EPS);
        Assert.assertEquals(6*42, evalExpr(ClauseType.TERM, "2*3*(42)"), EPS);
    }

    @Test(expected = ParsingException.class)
    public void testTermFail1() throws Exception {
        evalExpr(ClauseType.TERM, "*");
    }
    @Test(expected = ParsingException.class)
    public void testTermFail2() throws Exception {
        evalExpr(ClauseType.TERM, "/");
    }

    @Test
    public void testArguments() throws Exception {
        assertEquals(
                Arrays.asList(new Constant(1), new Constant(2)),
                parse(ClauseType.ARGUMENTS, "1,2"));

        assertEquals(
                Arrays.asList(new Variable("x"), new Variable("y")),
                parse(ClauseType.ARGUMENTS, "x,y"));

        assertEquals(
                Arrays.asList(new Variable("x"), new Variable("y")),
                parse(ClauseType.ARGUMENTS, "(x),y"));

        assertEquals(
                Arrays.asList(new Addition(new Variable("x"), new Variable("y")),
                        new Variable("y")),
                parse(ClauseType.ARGUMENTS, "(x+y),y"));
        assertEquals(
                Arrays.asList(new Addition(new Variable("x"), new Variable("y")),
                        new Variable("y")),
                parse(ClauseType.ARGUMENTS, "x+y,y"));
        assertEquals(
                Arrays.asList(
                        new Addition(new Variable("x"), new Variable("y")),
                        new Subtraction(new Variable("x"), new Variable("y"))
                ),
                parse(ClauseType.ARGUMENTS, "x+y,x-y"));
    }

    @Test(expected = ParsingException.class)
    public void testArgumentsFail1() throws Exception {
        parse(ClauseType.ARGUMENTS, "1,");
    }
    @Test(expected = ParsingException.class)
    public void testArgumentsFail2() throws Exception {
        parse(ClauseType.ARGUMENTS, ",1");
    }
    @Test(expected = ParsingException.class)
    public void testArgumentsFail3() throws ParsingException {
        parse(ClauseType.ARGUMENTS, "x+,");
    }
    @Test(expected = ParsingException.class)
    public void testArgumentsFail4() throws ParsingException {
        parse(ClauseType.ARGUMENTS, ",x+");
    }

    @Test
    public void testMathFunction() throws Exception {
        assertEquals(new MathFunction(MathFunction.Type.SIN, new Variable("x")),
                parse(ClauseType.MATH_FUNCTION, "sin(x)"));
        assertEquals(new MathFunction(MathFunction.Type.COS, new Variable("x")),
                parse(ClauseType.MATH_FUNCTION, "cos(x)"));
        assertEquals(new MathFunction(MathFunction.Type.SIGNUM, new Variable("x")),
                parse(ClauseType.MATH_FUNCTION, "sign(x)"));
        assertEquals(new MathFunction(MathFunction.Type.SIGNUM,
                new Addition(new Variable("x"), new Variable("y"))),
                parse(ClauseType.MATH_FUNCTION, "sign(x+y)"));
    }

    @Test(expected = ParsingException.class)
    public void testMathFunctionFail1() throws Exception {
        parse(ClauseType.MATH_FUNCTION, "sin(x,y)");
    }
    @Test(expected = ParsingException.class)
    public void testMathFunctionFail2() throws Exception {
        parse(ClauseType.MATH_FUNCTION, "sin(x+)");
    }
    @Test(expected = ParsingException.class)
    public void testMathFunctionFail3() throws Exception {
        parse(ClauseType.MATH_FUNCTION, "sin(x+1,)");
    }
    @Test(expected = ParsingException.class)
    public void testMathFunctionFail4() throws Exception {
        parse(ClauseType.MATH_FUNCTION, "somebadfunc(x)");
    }
}
