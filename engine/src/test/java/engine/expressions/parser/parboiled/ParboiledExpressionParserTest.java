package engine.expressions.parser.parboiled;

import engine.calculation.Arguments;
import engine.calculation.functions.*;
import engine.expressions.Equation;
import engine.expressions.Function;
import engine.expressions.parser.ClauseType;
import engine.expressions.parser.ParsingException;
import engine.expressions.parser.parboiled.ParboiledExpressionParser;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

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
    public void testParseExpression1() throws Exception {
        Function parsed = parser.parseExpression("2*(1+x+a+3)");

        Function inParents = Addition.sequence(one, xVar, aVar, three);
        Multiplication expected = new Multiplication(two, inParents);
        assertEquals(expected, parsed);
    }

    @Test
    public void testParseExpression2() throws Exception {
        Function parsed = parser.parseExpression("2*(1+x+a+3-5)");

        Function inParents = Addition.sequence(one, xVar, aVar, three);
        inParents = new Subtraction(inParents, five);
        Multiplication expected = new Multiplication(two, inParents);

        assertEquals(expected, parsed);
    }

    @Test
    public void testParseExpression3() throws Exception {
        Assert.assertEquals(
                new Subtraction(
                    new Subtraction(new Constant(1), new Constant(2)),
                        new Constant(3))
                , parse(ClauseType.EXPRESSION, "1-2-3"));
        Assert.assertEquals(
                new Addition(
                    new Addition(new Constant(1), new Constant(2)),
                        new Constant(3))
                , parse(ClauseType.EXPRESSION, "1+2+3"));

    }

    @Test(expected = ParsingException.class)
    public void testParseExpressionFail1() throws Exception {
        parseExpression("(");
    }

    @Test(expected = ParsingException.class)
    public void testParseExpressionFail2() throws Exception {
        parseExpression("1*");
    }

    @Test(expected = ParsingException.class)
    public void testParseExpressionFail3() throws Exception {
        parseExpression("()");
    }

    @Test(expected = ParsingException.class)
    public void testParseExpressionFail4() throws Exception {
        parseExpression("*1");
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
        return new ParboiledExpressionParser().parse(type, string);
    }

    private void parseExpression(String expr) throws ParsingException {
        new ParboiledExpressionParser().parseExpression(expr);
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
        Assert.assertEquals(
                new Division(
                    new Division(new Constant(1), new Constant(2)),
                        new Constant(3))
                , parse(ClauseType.TERM, "1/2/3"));
        Assert.assertEquals(
                new Multiplication(
                    new Multiplication(new Constant(1), new Constant(2)),
                        new Constant(3))
                , parse(ClauseType.TERM, "1*2*3"));
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
        assertEquals(new MathFunction(MathFunctionType.SIN, new Variable("x")),
                parse(ClauseType.MATH_FUNCTION, "sin(x)"));
        assertEquals(new MathFunction(MathFunctionType.COS, new Variable("x")),
                parse(ClauseType.MATH_FUNCTION, "cos(x)"));
        assertEquals(new MathFunction(MathFunctionType.SIGNUM, new Variable("x")),
                parse(ClauseType.MATH_FUNCTION, "sign(x)"));
        assertEquals(new MathFunction(MathFunctionType.SIGNUM,
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

    @Test
    public void testEquation() throws Exception {
        parse(ClauseType.EQUATION, "x=y");
        parse(ClauseType.EQUATION, "4=3");
        parse(ClauseType.EQUATION, "x=y+5");
        parse(ClauseType.EQUATION, "5<6");
        parse(ClauseType.EQUATION, "5>=6");
        parse(ClauseType.EQUATION, "5<=6");
        parse(ClauseType.EQUATION, "5>6");
    }

    @Test(expected=ParsingException.class)
    public void testEquationFail1() throws Exception {
        parse(ClauseType.EQUATION, "x=y=4");
    }
    @Test(expected=ParsingException.class)
    public void testEquationFail2() throws Exception {
        parse(ClauseType.EQUATION, "x=");
    }

    @Test
    public void testWhitespace() throws Exception {
        assertEquals(8.0, evalExpr(ClauseType.EXPRESSION, " 5 + 3 "), EPS);
        assertEquals(2.0, evalExpr(ClauseType.EXPRESSION, " 6 / ( 1 + 2 ) "), EPS);
        assertEquals(11.0, evalExpr(ClauseType.EXPRESSION, " 3 * 3 + 4 / 2 + sin ( 0 ) "), EPS);
    }

    @Test
    public void testEquations() throws Exception {
        assertEquals(Arrays.asList(
                new Equation(new Variable("x"), Equation.Type.EQUAL, new Variable("y")),
                new Equation(new Variable("x"), Equation.Type.LESS, new Multiplication(
                        new Variable("y"), new Variable("y")))),
                parse(ClauseType.EQUATIONS, " x= y x<y*y"));
        assertEquals(Arrays.asList(
                new Equation(new Variable("x"), Equation.Type.EQUAL, new Addition(new Variable("y"),
                        new Constant(5))),
                new Equation(new Addition(new Constant(5),new Variable("x")),
                        Equation.Type.LESS, new Multiplication(
                        new Variable("y"), new Variable("y")))),
                parse(ClauseType.EQUATIONS, " x= y+5 5+x<y*y"));
    }


}
