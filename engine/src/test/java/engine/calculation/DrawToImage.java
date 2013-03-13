package engine.calculation;

import engine.locus.PixelDrawer;
import engine.locus.RectRange;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
* User: Oleksiy Pylypenko
* Date: 3/13/13
* Time: 9:34 AM
*/
class DrawToImage implements PixelDrawer {

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
