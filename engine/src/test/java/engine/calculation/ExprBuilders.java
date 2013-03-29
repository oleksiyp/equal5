package engine.calculation;

import engine.calculation.functions.*;
import engine.expressions.Equation;
import engine.expressions.Function;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/30/13
 * Time: 12:01 AM
 */
public class ExprBuilders {
    public static Constant c(double v) {
        return new Constant(v);
    }
    public static Variable v(String n) {
        return new Variable(n);
    }
    public static Multiplication m(Function a, Function b) {
        return new Multiplication(a, b);
    }
    public static Subtraction s(Function a, Function b) {
        return new Subtraction(a, b);
    }
    public static Addition a(Function a, Function b) {
        return new Addition(a, b);
    }
    public static Division d(Function a, Function b) {
        return new Division(a, b);
    }
    public static Equation eq(Function leftExpr, Function rightExpr) {
        return new Equation(leftExpr, Equation.Type.EQUAL, rightExpr);
    }
}
