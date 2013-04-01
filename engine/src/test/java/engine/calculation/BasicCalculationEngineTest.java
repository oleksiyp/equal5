package engine.calculation;

import engine.calculation.evaluator.FunctionEvaluator;
import engine.calculation.evaluator.ImmediateFunctionEvaluator;
import engine.calculation.functions.*;
import engine.expressions.Equation;
import org.junit.Test;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/8/13
 * Time: 10:24 PM
 */
public class BasicCalculationEngineTest {
    @Test
    public void testCalculate() throws Exception {
        FunctionEvaluator evaluator = new ImmediateFunctionEvaluator();
        BasicCalculationEngine eng = new BasicCalculationEngine(evaluator);

        Equation eq = new Equation(new Variable("y"),
                                Equation.Type.EQUAL,
                                new Subtraction(new Constant(1),
                                new Power(
                                new Addition(new Division(new Variable("x"), new Constant(2)),
                                        new Constant(-0.5)),
                                        new Constant(2))));

        ViewportSize size = new ViewportSize(1000, 1000);
        CalculationParameters params = new CalculationParameters(
                new ViewportBounds(0, 0, 1, 1),
                size, 0, eq);
        eng.calculate(params);
    }

}
