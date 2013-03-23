package engine.calculation;

import engine.calculation.evaluator.FunctionEvaluator;
import engine.calculation.functions.MathFunction;
import engine.calculation.functions.Subtraction;
import engine.expressions.Equation;
import engine.expressions.Function;
import engine.locus.DiscreteLocus;
import engine.locus.Drawable;
import engine.locus.PixelDrawable;
import util.Cancelable;
import util.CancellationRoutine;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/8/13
 * Time: 7:37 PM
 */
public class BasicCalculationEngine implements CalculationEngine, Cancelable {
    private CancellationRoutine routine = CancellationRoutine.NO_ROUTINE;
    private final FunctionEvaluator evaluator;

    public BasicCalculationEngine(FunctionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public void setCancellationRoutine(CancellationRoutine routine) {
        this.routine = routine;
    }

    @Override
    public CalculationResults calculate(CalculationParameters parameters) {
        Equation[] equations = parameters.getEquations();

        Drawable []result = new Drawable[equations.length];
        for (int i = 0; i < equations.length; i++)
        {
            result[i] = buildDiscreteLocus(parameters, equations[i]);
        }
        return new CalculationResults(parameters, result);
    }

    private DiscreteLocus buildDiscreteLocus(CalculationParameters parameters, Equation equation) {
        int width = parameters.getSize().getWidth();
        int height = parameters.getSize().getHeight();

        double []row = new double[width + 1];
        double []prevRow = new double[width + 1];
        Function diff = new MathFunction(MathFunction.Type.SIGNUM,
                new Subtraction(equation.getLeftPart(),
                equation.getRightPart()));

        final double []coordinates = new double[3];
        Arguments arguments = new XYArguments(coordinates);

        int [][]locusData = new int[height][];

        ViewportBounds bounds = parameters.getBounds();

        coordinates[2] = parameters.getT();

        double xDelta = bounds.getXDelta(width);
        double yDelta = bounds.getYDelta(height);

        double xStart = bounds.getLeft() - xDelta / 2.0;
        double yStart = bounds.getTop() - yDelta / 2.0;

        for (int j = 0; j <= height; j++) {

            routine.checkCanceled();

            coordinates[1] = yStart + j * yDelta;
            for (int i = 0; i <= width; i++) {
                coordinates[0] = xStart + i * xDelta;
                row[i] = evaluator.calculate(diff, arguments);
            }

            if (j >= 1) {
                locusData[j - 1] = equation
                        .getType()
                        .accept(new LocusRowDiffVisitor(row, prevRow));
            }

            double []swap = row;
            row = prevRow;
            prevRow = swap;
        }

        return new DiscreteLocus(locusData);
    }

    private static class XYArguments implements Arguments {
        private final double[] coordinates;

        public XYArguments(double[] coordinates) {
            this.coordinates = coordinates;
        }

        @Override
        public String[] getArgumentNames() {
            return new String[]{
                    "x",
                    "y",
                    "t"
            };
        }

        @Override
        public double getValue(String name) {
            if ("x".equals(name)) {
                return coordinates[0];
            } else if ("y".equals(name)) {
                return coordinates[1];
            } else if ("t".equals(name)) {
                return coordinates[2];
            }
            throw new UnknownArgumentUsedException(name);
        }
    }

}
