package engine.expression.parser;

import engine.calculation.Arguments;
import engine.calculation.functions.*;
import engine.expressions.Equation;
import engine.expressions.Function;
import engine.expressions.parser.ClauseType;
import engine.expressions.parser.ExpressionParser;
import engine.expressions.parser.ParsingException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 12:49 PM
 */
public abstract class AbstractExpressionParserTest<EP extends ExpressionParser> {
    public static final double EPS = 1e-6;
    private EP parser;
    private Constant one;
    private Constant two;
    private Constant three;
    private Constant five;
    private Variable xVar;
    private Variable aVar;

    public AbstractExpressionParserTest(EP parser) {
        this.parser = parser;
    }

    @Before
    public void setUp() throws Exception {
        one = new Constant(1);
        two = new Constant(2);
        three = new Constant(3);
        five = new Constant(5);
        xVar = new Variable("x");
        aVar = new Variable("a");
    }

    @Test
    public void testParseExpression1() throws Exception {
        Function parsed = (Function) parse(ClauseType.ADDITIVE_EXPRESSION, "2*(1+x+a+3)");

        Function inParents = Addition.sequence(one, xVar, aVar, three);
        Multiplication expected = new Multiplication(two, inParents);
        assertEquals(expected, parsed);
    }

    @Test
    public void testParseExpression2() throws Exception {
        Function parsed = (Function) parse(ClauseType.ADDITIVE_EXPRESSION, "2*(1+x+a+3-5)");

        Function inParents = Addition.sequence(one, xVar, aVar, three);
        inParents = new Subtraction(inParents, five);
        Multiplication expected = new Multiplication(two, inParents);

        assertEquals(expected, parsed);
    }

    @Test
    public void testParseExpression3() throws Exception {
        assertEquals(
                new Subtraction(
                    new Subtraction(new Constant(1), new Constant(2)),
                        new Constant(3))
                , parse(ClauseType.ADDITIVE_EXPRESSION, "1-2-3"));
        assertEquals(
                new Addition(
                    new Addition(new Constant(1), new Constant(2)),
                        new Constant(3))
                , parse(ClauseType.ADDITIVE_EXPRESSION, "1+2+3"));

    }

    @Test(expected = ParsingException.class)
    public void testParseExpressionFail1() throws Exception {
        parse(ClauseType.ADDITIVE_EXPRESSION, "(");
    }

    @Test(expected = ParsingException.class)
    public void testParseExpressionFail2() throws Exception {
        parse(ClauseType.ADDITIVE_EXPRESSION, "1*");
    }

    @Test(expected = ParsingException.class)
    public void testParseExpressionFail3() throws Exception {
        parse(ClauseType.ADDITIVE_EXPRESSION, "()");
    }

    @Test(expected = ParsingException.class)
    public void testParseExpressionFail4() throws Exception {
        parse(ClauseType.ADDITIVE_EXPRESSION, "*1");
    }

    private Object parse(ClauseType type, String string) throws ParsingException {
        Object obj = parser.parse(type, string);
        if (type == ClauseType.ARGUMENTS) {
            Function []args = (Function[]) obj;
            return Arrays.asList(args);
        } else if (type == ClauseType.EQUATIONS) {
            Equation []args = (Equation[]) obj;
            return Arrays.asList(args);
        }
        return obj;
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

    @Test
    public void testConstant() throws Exception {
        assertEquals(123.0, parseConstant("123").getValue(), EPS);
        assertEquals(1e6, parseConstant("1e6").getValue(), EPS);
        assertEquals(1e6, parseConstant("1e+6").getValue(), EPS);
        assertEquals(EPS, parseConstant("0.1e-5").getValue(), EPS);
        assertEquals(25.25, parseConstant("2525e-2").getValue(), EPS);
    
        parse(ClauseType.CONSTANT, "1");
        parse(ClauseType.CONSTANT, "10");
        parse(ClauseType.CONSTANT, "10.5");
        parse(ClauseType.CONSTANT, "10.5e1");
        parse(ClauseType.CONSTANT, "10.e1");
        parse(ClauseType.CONSTANT, "10.5e-1");
        parse(ClauseType.CONSTANT, "10.e-1");
        parse(ClauseType.CONSTANT, ".5");
        parse(ClauseType.CONSTANT, ".5e-1");
        parse(ClauseType.CONSTANT, "1e1");
        parse(ClauseType.CONSTANT, "1e-1");
        parse(ClauseType.CONSTANT, "1e+1");

        for (int i = 0; i <= 9; i++) {
            parse(ClauseType.CONSTANT,
                    "" + i);
        }
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
    @Test(expected = ParsingException.class)
    public void testConstantFail8() throws Exception {
        parseConstant("e");
    }
    @Test(expected = ParsingException.class)
    public void testConstantFail9() throws Exception {
        parseConstant(".");
    }
    @Test(expected = ParsingException.class)
    public void testConstantFail10() throws Exception {
        parse(ClauseType.CONSTANT, "a");
    }
    @Test(expected = ParsingException.class)
    public void testConstantFail11() throws Exception {
        parse(ClauseType.CONSTANT, "0a");
    }
    @Test(expected = ParsingException.class)
    public void testConstantFail12() throws Exception {
        parse(ClauseType.CONSTANT, "aa");
    }

    private Constant parseConstant(String string) throws ParsingException {
        return (Constant) parse(ClauseType.CONSTANT, string);
    }

    @Test
    public void testParents() throws Exception {
        assertEquals(123, evalExpr(ClauseType.PARENTHESES, "(123)"), EPS);
        assertEquals(123, evalExpr(ClauseType.PARENTHESES, "((123))"), EPS);
        assertEquals(123, evalExpr(ClauseType.PARENTHESES, "(((123)))"), EPS);
        assertEquals(11, evalExpr(ClauseType.PARENTHESES, "(((5+6)))"), EPS);
        assertEquals(76, evalExpr(ClauseType.PARENTHESES, "(((5+6)+(3+2)*(8+5)))"), EPS);
    }

    @Test(expected = ParsingException.class)
    public void testParentsFail1() throws Exception {
        evalExpr(ClauseType.PARENTHESES, "(");
    }
    @Test(expected = ParsingException.class)
    public void testParentsFail2() throws Exception {
        evalExpr(ClauseType.PARENTHESES, ")");
    }
    @Test(expected = ParsingException.class)
    public void testParentsFail3() throws Exception {
        evalExpr(ClauseType.PARENTHESES, ")(");
    }
    @Test(expected = ParsingException.class)
    public void testParentsFail4() throws Exception {
        evalExpr(ClauseType.PARENTHESES, "(()");
    }
    @Test(expected = ParsingException.class)
    public void testParentsFail5() throws Exception {
        evalExpr(ClauseType.PARENTHESES, "((5)(5))");
    }

    private double evalExpr(ClauseType type, String expr) throws ParsingException {
        return ((Function) parse(type, expr)).eval(Arguments.EMPTY);
    }

    @Test
    public void testFactor() throws Exception {
        assertEquals(2, evalExpr(ClauseType.PRIMARY_EXPRESSION, "2"), EPS);
        assertEquals(2, evalExpr(ClauseType.PRIMARY_EXPRESSION, "(2)"), EPS);
        assertEquals(1.0, evalExpr(ClauseType.PRIMARY_EXPRESSION, "sign(5)"), EPS);
    }

    @Test(expected = ParsingException.class)
    public void testFactorFail1() throws Exception {
        evalExpr(ClauseType.PRIMARY_EXPRESSION, "1+1");
    }
    @Test(expected = ParsingException.class)
    public void testFactorFail2() throws Exception {
        evalExpr(ClauseType.PRIMARY_EXPRESSION, "2-1");
    }

    @Test
    public void testTerm() throws Exception {
        assertEquals(6, evalExpr(ClauseType.MULTIPLICATIVE_EXPRESSION, "2*3"), EPS);
        assertEquals(60, evalExpr(ClauseType.MULTIPLICATIVE_EXPRESSION, "2*5*6"), EPS);
        assertEquals(36*14, evalExpr(ClauseType.MULTIPLICATIVE_EXPRESSION, "2*3*6*(6+8)"), EPS);
        assertEquals(6*42, evalExpr(ClauseType.MULTIPLICATIVE_EXPRESSION, "2*3*(42)"), EPS);
        assertEquals(
                new Division(
                    new Division(new Constant(1), new Constant(2)),
                        new Constant(3))
                , parse(ClauseType.MULTIPLICATIVE_EXPRESSION, "1/2/3"));
        assertEquals(
                new Multiplication(
                    new Multiplication(new Constant(1), new Constant(2)),
                        new Constant(3))
                , parse(ClauseType.MULTIPLICATIVE_EXPRESSION, "1*2*3"));
    }

    @Test(expected = ParsingException.class)
    public void testTermFail1() throws Exception {
        evalExpr(ClauseType.MULTIPLICATIVE_EXPRESSION, "*");
    }
    @Test(expected = ParsingException.class)
    public void testTermFail2() throws Exception {
        evalExpr(ClauseType.MULTIPLICATIVE_EXPRESSION, "/");
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
        assertEquals(8.0, evalExpr(ClauseType.ADDITIVE_EXPRESSION, " 5 + 3 "), EPS);
        assertEquals(2.0, evalExpr(ClauseType.ADDITIVE_EXPRESSION, " 6 / ( 1 + 2 ) "), EPS);
        assertEquals(11.0, evalExpr(ClauseType.ADDITIVE_EXPRESSION, " 3 * 3 + 4 / 2 + sin ( 0 ) "), EPS);
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


    @Test
    public void testVarList() throws Exception {
        parser.setVarList(Arrays.asList("x"));
        parser.setKnownConstants(Collections.singletonMap("pi", 3.0));
        assertEquals(new Addition(new Variable("x"), new Constant(3.0)),
                parser.parse(ClauseType.ADDITIVE_EXPRESSION, "x+pi"));

    }

    @Test(expected = ParsingException.class)
    public void testVarListFail() throws Exception {
        parser.setVarList(Arrays.asList("x"));
        parser.setKnownConstants(Collections.singletonMap("pi", 3.0));
        assertEquals(new Addition(new Variable("x"), new Constant(3.0)),
                parser.parse(ClauseType.ADDITIVE_EXPRESSION, "y"));

    }
}
