package engine.calculation;

import engine.calculation.functions.*;
import engine.expressions.Calculable;
import engine.expressions.Equation;

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
    public static Multiplication m(Calculable a, Calculable b) {
        return new Multiplication(a, b);
    }
    public static Subtraction s(Calculable a, Calculable b) {
        return new Subtraction(a, b);
    }
    public static Addition a(Calculable a, Calculable b) {
        return new Addition(a, b);
    }
    public static Division d(Calculable a, Calculable b) {
        return new Division(a, b);
    }
    public static Equation eq(Calculable leftExpr, Calculable rightExpr) {
        return new Equation(leftExpr, Equation.Type.EQUAL, rightExpr);
    }
}
