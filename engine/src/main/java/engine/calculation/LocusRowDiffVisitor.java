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

            if (sign(a) == -1) {
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

            int s = sign(a) + sign(b) + sign(c) + sign(d);
            if (-4 < s && s < 4) {
                ret[sz++] = i;
            }
        }

        return Arrays.copyOf(ret, sz);
    }

    private int sign(double a) {
        if (a < 0) return -1;
        if (a > 0) return 1;
        return 0;
    }

    @Override
    public int[] greater() {
        int []ret = new int[row.length - 1];
        int sz = 0;

        for (int i = 0; i < row.length - 1; i++) {
            double a = row[i];

            if (sign(a) == 1) {
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
