package engine.locus;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:18 PM
 */
public interface PixelDrawable {
    RectRange getSize();

    void draw(RectRange range, PixelDrawer drawer);
}
