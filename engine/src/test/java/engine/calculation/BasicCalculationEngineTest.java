package engine.calculation;

import engine.calculation.functions.*;
import engine.expressions.Equation;
import engine.expressions.Name;
import engine.locus.PixelDrawable;
import engine.locus.PixelDrawer;
import engine.locus.RectRange;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

        PixelDrawable drawable = eng.calculate(800, 800,
                new Equation(new Variable(new Name("y")),
                        Equation.Type.EQUAL,
                        new Subtraction(new Constant(1),
                        new Power(
                        new Addition(new Division(new Variable(new Name("x")), new Constant(2)),
                                new Constant(-0.5)),
                                new Constant(2)))));


        DrawToImage drawer = new DrawToImage(drawable.getSize());
        drawable.draw(drawable.getSize(), drawer);
        drawer.writePng(new File("test1.png"));
    }

    private class DrawToImage implements PixelDrawer {

        private final BufferedImage image;

        public DrawToImage(RectRange size) {
            image = new BufferedImage(size.getWidth(),
                    size.getHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = image.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, size.getWidth(), size.getHeight());
        }

        @Override
        public void put(int x, int y) {
            image.setRGB(x, y, 0xFF000000);
        }

        public void writePng(File file) throws IOException {
            ImageIO.write(image, "PNG", file);
        }
    }
}
