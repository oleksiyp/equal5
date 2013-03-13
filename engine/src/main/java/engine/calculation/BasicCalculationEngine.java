package engine.calculation;

import engine.calculation.functions.Subtraction;
import engine.expressions.Equation;
import engine.expressions.Function;
import engine.locus.DiscreteLocus;
import engine.locus.PixelDrawable;

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

            final double []coordinates = new double[2];
            Arguments arguments = new XYArguments(coordinates);

            int [][]locusData = new int[height][];

            for (int y = 0; y <= height; y++) {
                coordinates[1] = ((double)y) / (height + 1);
                for (int x = 0; x <= width; x++) {
                    coordinates[0] = ((double)x) / (width + 1);
                    row[x] = evaluator.calculate(diff, arguments);
                }

                if (y >= 1) {
                    locusData[y - 1] = equation
                            .getType()
                            .accept(new LocusRowDiffVisitor(row, prevRow));
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
        private final double[] coordinates;

        public XYArguments(double[] coordinates) {
            this.coordinates = coordinates;
        }

        @Override
        public String[] getArguments() {
            return new String[]{
                    "x",
                    "y"
            };
        }

        @Override
        public double getValue(String name) {
            if ("x".equals(name)) {
                return coordinates[0];
            } else if ("y".equals(name)) {
                return coordinates[1];
            }
            throw new UnknownArgumentUsedException(name);
        }
    }

}
