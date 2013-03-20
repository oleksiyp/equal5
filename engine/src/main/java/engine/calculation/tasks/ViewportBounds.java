package engine.calculation.tasks;

public class ViewportBounds {
    private static final double EPSILON = Double.MIN_NORMAL * (1 << 16);

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ViewportBounds that = (ViewportBounds) o;

        if (Double.compare(that.bottom, bottom) != 0) return false;
        if (Double.compare(that.left, left) != 0) return false;
        if (Double.compare(that.right, right) != 0) return false;
        if (Double.compare(that.top, top) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = left != +0.0d ? Double.doubleToLongBits(left) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = top != +0.0d ? Double.doubleToLongBits(top) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = bottom != +0.0d ? Double.doubleToLongBits(bottom) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = right != +0.0d ? Double.doubleToLongBits(right) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public double getXDelta(int divisor) {
        return getWidth() / divisor;
    }

    public double getYDelta(int divisor) {
        return getHeight() / divisor;
    }
}