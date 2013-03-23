package engine.locus;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:18 PM
 */
public interface PixelDrawable extends Drawable {
    void draw(RectRange range, PixelDrawer drawer);
}
