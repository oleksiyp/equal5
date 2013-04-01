package engine.calculation.drawables;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
* User: Oleksiy Pylypenko
* Date: 3/13/13
* Time: 9:34 AM
*/
public class DrawToImage implements RowDrawer, PixelDrawer {
    public static final AlphaComposite CLEAR_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.CLEAR);
    private BufferedImage image;

    public BufferedImage getImage() {
        return image;
    }

    public DrawToImage(RectRange size) {
        resize(size);
    }

    public void resize(RectRange size) {
        if (image != null
                && image.getWidth() >= size.getWidth()
                && image.getHeight() >= size.getHeight()) {
            clear();
            return;
        }

        int newW = size.getWidth() * 3 / 2;
        int newH = size.getHeight() * 3 / 2;
        image = new BufferedImage(
                newW,
                newH,
                BufferedImage.TYPE_INT_ARGB);

        clear();
    }

    private void clear() {
        Graphics2D g = image.createGraphics();
        try {
            Composite saveComposite = g.getComposite();
            g.setComposite(CLEAR_COMPOSITE);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.setComposite(saveComposite);
        } finally {
            g.dispose();
        }
    }


    public void writePng(File file, RectRange range) throws IOException {
        BufferedImage cropImg = new BufferedImage(
                range.getWidth(),
                range.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = cropImg.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, range.getWidth(), range.getHeight());
            g.drawImage(image,
                    0, 0, range.getWidth(), range.getHeight(),
                    range.getMinX(), range.getMinY(),
                    range.getWidth(), range.getHeight(),
                    null);
        } finally {
            g.dispose();
        }
        ImageIO.write(cropImg, "PNG", file);
    }

    @Override
    public void drawRow(int y, int start, int end, int[] row) {
        for (int i = start; i < end; i++) {
            image.setRGB(row[i], y, 0xFF000000);
        }
    }

    @Override
    public void put(int x, int y) {
        image.setRGB(x, y, 0xFF000000);
    }

    public void draw(RectRange range, Drawable drawable) {
        if (drawable instanceof PixelDataDrawable) {
            WritableRaster raster = image.getRaster();
            DataBufferInt dataBuffer = (DataBufferInt) raster.getDataBuffer();
            int[] pixelData = dataBuffer.getData();
            ((PixelDataDrawable)drawable).draw(range,
                    pixelData,
                    image.getWidth(),
                    image.getHeight()
            );
        } else if (drawable instanceof RowDrawable) {
            ((RowDrawable)drawable).draw(range, this);
        } else if (drawable instanceof PixelDrawable) {
            ((PixelDrawable)drawable).draw(range, this);
        } else {
            throw new UnsupportedOperationException("operation type not supported on '" + drawable + "'");
        }
    }
}
