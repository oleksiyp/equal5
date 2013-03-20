package engine.calculation;

import engine.calculation.tasks.CalculationParameters;
import engine.calculation.tasks.CalculationResults;
import engine.calculation.tasks.ViewportBounds;
import engine.calculation.tasks.ViewportSize;
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

import static org.junit.Assert.fail;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/8/13
 * Time: 10:24 PM
 */
public class VectorCalculationEngineTest {
    private static final File DIR = new File("testimgs");

    private CalculationEngine eng;

    @Before
    public void setUp() throws Exception {
        DIR.mkdirs();
        eng = new VectorCalculationEngine();
    }

    @Test
    public void testCalculate() throws Exception {
        Equation eq1 = eq("y", "1-(x/2-0.5)*(x/2-0.5)");
        Equation eq2 = eq("(x-0.5)*(x-0.5)+(y-0.5)*(y-0.5)", "0.1");
        Equation eq3 = eq("(x-0.5)*(x-0.5)*2+(y-0.5)*(y-0.5)", "0.1");
        draw("test1.png", eq1);
        draw("test2.png", eq2);
        draw("test3.png", eq3);
    }


    void draw(String file, Equation... eqs) {
        ViewportSize size = new ViewportSize(1000, 1000);
        CalculationParameters params = new CalculationParameters(
                new ViewportBounds(0, 0, 1, 1),
                size, eqs);
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
            Function leftExpr = ep.parseFunction(left);
            Function rightExpr = ep.parseFunction(right);
            return new Equation(leftExpr, Equation.Type.EQUAL, rightExpr);
        } catch (ParsingException e) {
            throw new RuntimeException(e);
        }
    }
}
