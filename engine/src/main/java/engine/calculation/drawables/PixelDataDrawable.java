package engine.calculation.drawables;

/**
 * User: Oleksiy Pylypenko
 * At: 4/1/13  6:42 PM
 */
public interface PixelDataDrawable extends Drawable {
    void draw(RectRange range, int[] pixelData, int width, int height);
}
