package engine.calculation;

import engine.expressions.Equation;

import java.util.Arrays;

/**
* User: Oleksiy Pylypenko
* At: 3/13/13  12:57 PM
*/
class LocusRowDiffVisitor implements Equation.TypeVisitor<int[]> {
    private final double[] row;
    private final double[] prevRow;

    public LocusRowDiffVisitor(double[] row, double[] prevRow) {
        this.row = row;
        this.prevRow = prevRow;
    }

    @Override
    public int[] less() {
        int []ret = new int[row.length - 1];
        int sz = 0;

        for (int i = 0; i < row.length - 1; i++) {
            double a = row[i];

            if (a == -1.0) {
                ret[sz++] = i;
            }
        }

        return Arrays.copyOf(ret, sz);
    }

    @Override
    public int[] equal() {
        int []ret = new int[row.length - 1];
        int sz = 0;

        for (int i = 0; i < row.length - 1; i++) {
            double a = row[i], b = row[i + 1],
                    c = prevRow[i], d = prevRow[i + 1];

            double s = a + b + c + d;
            if (-4.0 < s && s < 4.0) {
                ret[sz++] = i;
            }
        }

        return Arrays.copyOf(ret, sz);
    }

    @Override
    public int[] greater() {
        int []ret = new int[row.length - 1];
        int sz = 0;

        for (int i = 0; i < row.length - 1; i++) {
            double a = row[i];

            if (a == 1.0) {
                ret[sz++] = i;
            }
        }

        return Arrays.copyOf(ret, sz);
    }

    @Override
    public int[] lessEqual() {
        return less();
    }

    @Override
    public int[] greaterEqual() {
        return greater();
    }
}
