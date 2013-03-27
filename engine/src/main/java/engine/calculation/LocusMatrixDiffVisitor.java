package engine.calculation;

import engine.expressions.Equation;

import java.util.Arrays;

/**
* User: Oleksiy Pylypenko
* At: 3/13/13  12:57 PM
*/
class LocusMatrixDiffVisitor implements Equation.TypeVisitor<int[][]> {

    private final double[] matrix;
    private final int width;
    private final int height;

    public LocusMatrixDiffVisitor(double[] matrix, int width) {
        this.matrix = matrix;
        this.width = width;
        this.height = matrix.length / width;
    }

    @Override
    public int[][] less() {
        int [][]ret = new int[height - 1][];
        for (int j = 0; j < height - 1; j++) {
            int []rowRet = new int[width - 1];
            int sz = 0;

            int k = j * width;
            for (int i = 0; i < width - 1; i++, k++) {
                double a = matrix[k];

                if (a == -1.0) {
                    rowRet[sz++] = i;
                }
            }
            ret[j] = Arrays.copyOf(rowRet, sz);
        }

        return ret;
    }

    @Override
    public int[][] equal() {
        int [][]ret = new int[height - 1][];
        for (int j = 0; j < height - 1; j++) {
            int []rowRet = new int[width - 1];
            int sz = 0;

            int k = j * width;
            int k2 = (j+1) * width;
            for (int i = 0; i < width - 1; i++, k++, k2++) {
                double a = matrix[k], b = matrix[k + 1],
                        c = matrix[k2], d = matrix[k2 + 1];

                double s = a + b + c + d;
                if (-4.0 < s && s < 4.0) {
                    rowRet[sz++] = i;
                }
            }
            ret[j] = Arrays.copyOf(rowRet, sz);
        }

        return ret;
    }

    @Override
    public int[][] greater() {
        int [][]ret = new int[height - 1][];
        for (int j = 0; j < height - 1; j++) {
            int []rowRet = new int[width - 1];
            int sz = 0;

            int k = j * width;
            for (int i = 0; i < width - 1; i++, k++) {
                double a = matrix[k];

                if (a == 1.0) {
                    rowRet[sz++] = i;
                }
            }
            ret[j] = Arrays.copyOf(rowRet, sz);
        }

        return ret;
    }

    @Override
    public int[][] lessEqual() {
        return less();
    }

    @Override
    public int[][] greaterEqual() {
        return greater();
    }
}
