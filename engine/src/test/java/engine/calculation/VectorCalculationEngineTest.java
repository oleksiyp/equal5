package engine.calculation;

import engine.calculation.vector.implementations.VectorMachineBuilder;
import engine.calculation.vector.VectorMachineEvaluator;
import engine.expressions.Equation;
import engine.expressions.Function;
import engine.expressions.ParboiledExpressionParser;
import engine.expressions.ParsingException;
import engine.locus.DrawToImage;
import engine.locus.PixelDrawable;
import engine.locus.RectRange;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

import static org.junit.Assert.fail;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/8/13
 * Time: 10:24 PM
 */
public class VectorCalculationEngineTest {
    private static final File DIR = new File("test_images");

    private CalculationEngine eng;

    @Before
    public void setUp() throws Exception {
        DIR.mkdirs();
        VectorMachineBuilder builder = new VectorMachineBuilder();
        builder.setConcurrency(2, Executors.newFixedThreadPool(2));
        VectorMachineEvaluator evaluator = new VectorMachineEvaluator(builder);
        eng = new VectorCalculationEngine(evaluator);
    }

    @Test
    public void testCalculate() throws Exception {
        Equation eq1 = eq("y", "1-(x/2-0.5)*(x/2-0.5)");
        Equation eq2 = eq("(x-0.5)*(x-0.5)+(y-0.5)*(y-0.5)", "0.1");
        Equation eq3 = eq("(x-0.5)*(x-0.5)*2+(y-0.5)*(y-0.5)", "0.1");
        draw("vector_machine1.png", eq1);
        draw("vector_machine2.png", eq2);
        draw("vector_machine3.png", eq3);
    }


    void draw(String file, Equation... eqs) {
        ViewportSize size = new ViewportSize(1000, 1000);
        CalculationParameters params = new CalculationParameters(
                new ViewportBounds(0, 0, 1, 1),
                size, 0, eqs);
        CalculationResults results = eng.calculate(params);

        RectRange range = RectRange.fromViewportSize(size);
        DrawToImage drawer = new DrawToImage(range);
        for (PixelDrawable drawable : results.getDrawables()) {
            drawable.draw(range, drawer);
        }
        try {
            drawer.writePng(new File(DIR, file));
        } catch (IOException e) {
            fail("IOException: " + e);
        }
    }

    private Equation eq(String left, String right) {
        ParboiledExpressionParser ep = new ParboiledExpressionParser();
        try {
            Function leftExpr = ep.parseExpression(left);
            Function rightExpr = ep.parseExpression(right);
            return new Equation(leftExpr, Equation.Type.EQUAL, rightExpr);
        } catch (ParsingException e) {
            throw new RuntimeException(e);
        }
    }
}
