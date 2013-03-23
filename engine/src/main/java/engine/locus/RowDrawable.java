package engine.locus;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/23/13
 * Time: 11:21 AM
 */
public interface RowDrawable extends Drawable {
    void draw(RectRange range, RowDrawer drawer);
}
