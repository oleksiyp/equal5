package engine.calculation;

import engine.calculation.vector.VectorMachineEvaluator;
import engine.calculation.vector.implementations.VectorMachineBuilder;
import engine.expressions.Equation;
import engine.expressions.Function;
import engine.expressions.parser.ClauseType;
import engine.expressions.parser.ParsingException;
import engine.expressions.parser.parboiled.ParboiledExpressionParser;
import engine.locus.DrawToImage;
import engine.locus.Drawable;
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
public class VectorCalculationEngine2Test {
    private static final File DIR = new File("test_images");

    private CalculationEngine eng;

    @Before
    public void setUp() throws Exception {
        DIR.mkdirs();
        VectorMachineBuilder builder = new VectorMachineBuilder();
        builder.setConcurrency(2, Executors.newFixedThreadPool(2));
        VectorMachineEvaluator evaluator = new VectorMachineEvaluator(builder);
        eng = new VectorCalculationEngine2(evaluator);
    }

    @Test
    public void testCalculate() throws Exception {
        Equation eq1 = eq("y", "1-(x/2-0.5)*(x/2-0.5)");
        Equation eq2 = eq("(x-0.5)*(x-0.5)+(y-0.5)*(y-0.5)", "0.1");
        Equation eq3 = eq("(x-0.5)*(x-0.5)*2+(y-0.5)*(y-0.5)", "0.1");
        draw("vector_calc2_1.png", eq1);
        draw("vector_calc2_2.png", eq2);
        draw("vector_calc2_3.png", eq3);
    }


    void draw(String file, Equation... eqs) {
        ViewportSize size = new ViewportSize(1000, 1000);
        CalculationParameters params = new CalculationParameters(
                new ViewportBounds(0, 0, 1, 1),
                size, 0, eqs);
        CalculationResults results = eng.calculate(params);

        RectRange range = RectRange.fromViewportSize(size);
        DrawToImage drawer = new DrawToImage(range);
        for (Drawable drawable : results.getDrawables()) {
            drawer.draw(drawable.getSize(), drawable);
        }
        try {
            drawer.writePng(new File(DIR, file), range);
        } catch (IOException e) {
            fail("IOException: " + e);
        }
    }

    private Equation eq(String left, String right) {
        ParboiledExpressionParser ep = new ParboiledExpressionParser();
        try {
            Function leftExpr = (Function) ep.parse(ClauseType.EXPRESSION, left);
            Function rightExpr = (Function) ep.parse(ClauseType.EXPRESSION, right);
            return new Equation(leftExpr, Equation.Type.EQUAL, rightExpr);
        } catch (ParsingException e) {
            throw new RuntimeException(e);
        }
    }
}
