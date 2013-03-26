package engine.calculation;

import java.awt.*;

public class ViewportSize {
    private final int width;
    private final int height;

    public ViewportSize(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("width or height is negative");
        }
        this.width = width;
        this.height = height;
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

        ViewportSize that = (ViewportSize) o;

        if (height != that.height) return false;
        if (width != that.width) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }

    public Dimension toDimension() {
        return new Dimension(width, height);
    }


    public boolean isEmpty() {
        return width == 0 || height == 0;
    }
}