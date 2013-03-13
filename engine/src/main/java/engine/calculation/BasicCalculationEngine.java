package engine.calculation;

import engine.calculation.functions.Subtraction;
import engine.expressions.Equation;
import engine.expressions.Function;
import engine.expressions.Name;
import engine.locus.DiscreteLocus;
import engine.locus.PixelDrawable;

import java.util.Arrays;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/8/13
 * Time: 7:37 PM
 */
public class BasicCalculationEngine implements CalculationEngine {
    private final FunctionEvaluator evaluator;

    public BasicCalculationEngine(FunctionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public PixelDrawable []calculate(int width, int height,
                                   Equation []equations) {
        PixelDrawable []result = new PixelDrawable[equations.length];
        for (int i = 0; i < equations.length; i++)
        {
            Equation equation = equations[i];
            double []row = new double[width + 1];
            double []prevRow = new double[width + 1];
            Function diff = new Subtraction(equation.getLeftPart(),
                    equation.getRightPart());

            final double []coords = new double[2];
            Arguments arguments = new XYArguments(coords);

            int [][]locusData = new int[height][];

            for (int y = 0; y <= height; y++) {
                coords[1] = ((double)y) / (height + 1);
                for (int x = 0; x <= width; x++) {
                    coords[0] = ((double)x) / (width + 1);
                    row[x] = evaluator.calculate(diff, arguments);
                }

                if (y >= 1) {
                    locusData[y - 1] = equation
                            .getType()
                            .accept(new RowDifferentiator(row, prevRow));
                }

                double []swap = row;
                row = prevRow;
                prevRow = swap;
            }

            result[i] = new DiscreteLocus(locusData);
        }
        return result;
    }

    private static class XYArguments implements Arguments {
        private final double[] coords;

        public XYArguments(double[] coords) {
            this.coords = coords;
        }

        @Override
        public Name[] getArguments() {
            return new Name[]{
                    new Name("x"),
                    new Name("y")
            };
        }

        @Override
        public double getValue(Name name) {
            if ("x".equals(name.getSymbols())) {
                return coords[0];
            } else if ("y".equals(name.getSymbols())) {
                return coords[1];
            }
            throw new UnknownArgumentUsedException(name);
        }
    }

    private static class RowDifferentiator implements Equation.TypeVisitor<int[]> {
        private final double[] row;
        private final double[] prevRow;

        public RowDifferentiator(double[] row, double[] prevRow) {
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
}
