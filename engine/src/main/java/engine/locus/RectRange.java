package engine.locus;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:14 PM
 */
public class RectRange {
    int minX, minY, width, height;

    public RectRange(int minX, int minY, int width, int height) {
        this.minX = minX;
        this.minY = minY;
        this.width = width;
        this.height = height;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RectRange rectRange = (RectRange) o;

        if (height != rectRange.height) return false;
        if (minX != rectRange.minX) return false;
        if (minY != rectRange.minY) return false;
        if (width != rectRange.width) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = minX;
        result = 31 * result + minY;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }
}
