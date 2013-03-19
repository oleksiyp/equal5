package engine.calculation;

import engine.calculation.functions.*;
import engine.expressions.Equation;
import engine.locus.DrawToImage;
import engine.locus.PixelDrawable;
import org.junit.Test;

import java.io.File;

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

        eng.setSize(800, 800);
        PixelDrawable drawable = eng.calculate(new Equation[]{eq})[0];

        DrawToImage drawer = new DrawToImage(drawable.getSize());
        drawable.draw(drawable.getSize(), drawer);
        drawer.writePng(new File("test1.png"));
    }

}
