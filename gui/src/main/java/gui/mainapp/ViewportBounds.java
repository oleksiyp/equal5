package gui.mainapp;

public class ViewportBounds {
    private static final double EPSILON = Double.MIN_NORMAL * 100000;

    private final double left;
    private final double top;
    private final double bottom;
    private final double right;


    public ViewportBounds(double left, double top, double bottom, double right) {
        if (Math.abs(left - right) < EPSILON) {
            throw new IllegalArgumentException("left and right bounds are equal(with regard to epsilon)");
        }
        if (Math.abs(top - bottom) < EPSILON) {
            throw new IllegalArgumentException("top and bottom bounds are equal(with regard to epsilon)");
        }

        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public double getLeft() {
        return left;
    }

    public double getTop() {
        return top;
    }

    public double getBottom() {
        return bottom;
    }

    public double getRight() {
        return right;
    }

    public ViewportBounds offset(double ox, double oy) {
        return new ViewportBounds(
                left + ox,
                top + oy,
                bottom + oy,
                right + ox);
    }

    public double getWidth() {
        return right - left;
    }

    public double getHeight() {
        return bottom - top;
    }

    public double getCenterX() {
        return (right + left) / 2;
    }

    public double getCenterY() {
        return (top + bottom) / 2;
    }

    public ViewportBounds zoom(double coef) {
        double cx = getCenterX();
        double cy = getCenterY();
        double dx = getWidth();
        double dy = getHeight();

        dx = dx * coef;
        dy = dy * coef;

        return new ViewportBounds(cx - dx / 2,
                cy - dy / 2,
                cy + dy / 2,
                cx + dx / 2);
    }
}