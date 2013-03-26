package gui.mainapp.viewport;

import engine.calculation.CalculationParameters;
import engine.calculation.ViewportBounds;
import engine.calculation.ViewportSize;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/26/13
 * Time: 12:11 AM
 */
public class CoordinateSystemTest {
    private static final File DIR = new File("test_images");
    static {
        DIR.mkdirs();
    }

    @Test
    public void testDraw() throws Exception {
        ViewportBounds bounds = new ViewportBounds(-12, -12, 12, 12);
        ViewportSize size = new ViewportSize(500, 500);
        CoordinateSystem system = new CoordinateSystem();
        BufferedImage img = new BufferedImage(
                size.getWidth(),
                size.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics g = img.getGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0,
                    size.getWidth(),
                    size.getHeight());
            system.draw(g, size, bounds);
        }finally {
            g.dispose();
        }
        ImageIO.write(img, "png", new File(DIR, "coord_sys1.png"));
    }
}
