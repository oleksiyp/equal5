package engine.calculation.drawables.matrix;

import engine.calculation.drawables.*;
import engine.expressions.Equation;

/**
 * User: Oleksiy Pylypenko
 * At: 4/1/13  5:53 PM
 */
public class MatrixDrawable implements PixelDataDrawable {
    private double[] matrix;
    private int width;
    private int height;
    private final RectRange range;

    public MatrixDrawable(Equation.Type eqType, double[] matrix, int width, int height) {
        this.matrix = matrix;
        this.width = width;
        this.height = height;
        range = new RectRange(0, 0, width - 1, height - 1);
    }

    @Override
    public RectRange getSize() {
        return range;
    }

    @Override
    public void draw(RectRange range, int[] pixelData,
                     int pxlDataWidth, int pxlDataHeight) {

        int offv = 0;
        int moff = 0;

        int minWidth = Math.min(range.getWidth(), Math.min(pxlDataWidth, width - 1));
        int minHeight = Math.min(range.getHeight(), height - 1);

        for (int j = 0; j < minHeight; j++) {
            for (int i = 0; i < minWidth; i++) {

                int moffI = moff + i;
                int a = (int) matrix[moffI], b = (int) matrix[moffI + 1],
                        c = (int) matrix[moffI + width], d = (int) matrix[moffI + width + 1];

                int s = a + b + c + d;
                if (-3 <= s && s <= 3) {
                    pixelData[offv + i] = 0xFF000000;
                }
            }
            offv += pxlDataWidth;
            moff += width;
        }
    }
}
