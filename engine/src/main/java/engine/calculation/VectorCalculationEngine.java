package engine.calculation;

import engine.calculation.drawables.Drawable;
import engine.calculation.functions.MathFunction;
import engine.calculation.functions.MathFunctionType;
import engine.calculation.functions.Subtraction;
import engine.calculation.vector.*;
import engine.calculation.vector.fillers.ConstantVectorFiller;
import engine.calculation.vector.fillers.LinearVectorFiller;
import engine.calculation.vector.fillers.VectorFiller;
import engine.expressions.Equation;
import engine.expressions.Function;
import engine.calculation.drawables.locus.DiscreteLocus;
import util.Cancelable;
import util.CancellationRoutine;
import util.VectorUtils;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/13/13
 * Time: 11:26 AM
 */
public class VectorCalculationEngine implements CalculationEngine, Cancelable {
    private final VectorEvaluator evaluator;
    private CancellationRoutine routine = CancellationRoutine.NO_ROUTINE;

    public VectorCalculationEngine(VectorEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public void setCancellationRoutine(CancellationRoutine routine) {
        this.routine = routine;
    }

    @Override
    public CalculationResults calculate(CalculationParameters parameters) {
        Equation[] equations = parameters.getEquations();
        int width = parameters.getSize().getWidth();
        int height = parameters.getSize().getHeight();

        int nEq = equations.length;

        Drawable[]ret = new Drawable[nEq];
        Function[] multiFunction = new Function[nEq];

        for (int i = 0; i < nEq; i++)
        {
            Equation equation = equations[i];
            multiFunction[i] = new MathFunction(
                    MathFunctionType.SIGNUM,
                    new Subtraction(
                            equation.getLeftPart(),
                            equation.getRightPart()));
        }


        evaluator.setSize(width + 1);
        evaluator.setFunctions(multiFunction);

        evaluator.prepare();

        int [][][]locusData = new int[nEq][height][];

        ViewportBounds bounds = parameters.getBounds();

        double t = parameters.getT();

        double xDelta = bounds.getXDelta(width);
        double yDelta = bounds.getYDelta(height);

        double xStart = bounds.getLeft() - xDelta / 2.0;
        double yStart = bounds.getTop() - yDelta / 2.0;

        XYTVectorArguments arguments = new XYTVectorArguments(xStart, xDelta, t);

        double [][]prevMatrix = null;
        for (int j = 0; j <= height; j++) {
            routine.checkCanceled();

            arguments.setY(yStart + j * yDelta);

            double[][] matrix = evaluator.calculate(arguments);

            if (prevMatrix == null) {
                prevMatrix = VectorUtils.copy(matrix);
                continue;
            }

            for (int i = 0; i < nEq; i++) {
                Equation.Type eqType = equations[i].getType();

                double []row = matrix[i];
                double []prevRow = prevMatrix[i];

                locusData[i][j - 1] = eqType
                        .accept(new LocusRowDiffVisitor(row, prevRow));
            }

            prevMatrix = VectorUtils.copy(matrix);
        }

        for (int i = 0; i < nEq; i++) {
            ret[i] = new DiscreteLocus(locusData[i]);
        }

        return new CalculationResults(parameters, ret);
    }

    private class XYTVectorArguments implements VectorArguments {
        private final ConstantVectorFiller yFiller;
        private final ConstantVectorFiller tFiller;
        private final LinearVectorFiller xFiller;

        public XYTVectorArguments(double xStart, double xDelta, double t) {
            yFiller = new ConstantVectorFiller(0);
            tFiller = new ConstantVectorFiller(t);
            xFiller = new LinearVectorFiller(xStart, xDelta);
        }

        @Override
        public String[] getArgumentNames() {
            return new String[] { "x", "y", "t" };
        }

        public void setY(double y) {
            yFiller.setValue(y);
        }

        @Override
        public VectorFiller getVectorFiller(String argument) {
            if ("x".equals(argument)) {
                return xFiller;
            } else if ("y".equals(argument)) {
                return yFiller;
            } else if ("t".equals(argument)) {
                return tFiller;
            }
            throw new UnknownArgumentUsedException(argument);
        }
    }
}
