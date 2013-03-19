package engine.calculation;

import engine.calculation.functions.Subtraction;
import engine.calculation.vector.*;
import engine.calculation.vector.fillers.ConstantVectorFiller;
import engine.calculation.vector.fillers.LinearVectorFiller;
import engine.calculation.vector.fillers.VectorFiller;
import engine.expressions.Equation;
import engine.expressions.Function;
import engine.locus.DiscreteLocus;
import engine.locus.PixelDrawable;
import util.CancellationRoutine;

import java.util.Arrays;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/13/13
 * Time: 11:26 AM
 */
public class VectorCalculationEngine implements CalculationEngine {
    private final ConcurrentVectorEvaluator evaluator = new VectorMachineEvaluator();

    private CancellationRoutine routine = CancellationRoutine.NO_ROUTINE;

    private int width, height;

    @Override
    public void setCancellationRoutine(CancellationRoutine routine) {
        this.routine = routine;
    }

    @Override
    public void setSize(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width or height");
        }
        this.width = width;
        this.height = height;
    }

    @Override
    public PixelDrawable[] calculate(Equation[] equations) {
        if (width == 0 || height == 0) {
            throw new IllegalStateException("call setSize before");
        }

        int nEq = equations.length;

        PixelDrawable []ret = new PixelDrawable[nEq];
        Function[] multiFunction = new Function[nEq];

        for (int i = 0; i < nEq; i++)
        {
            Equation equation = equations[i];
            multiFunction[i] = new Subtraction(equation.getLeftPart(),
                    equation.getRightPart());
        }


        evaluator.setSize(width + 1);
        evaluator.setFunctions(multiFunction);
        evaluator.setConcurrency(2);

        evaluator.prepare();

        int [][][]locusData = new int[nEq][height][];

        double xDelta = 1.0 / (double) (width + 1);
        XYTVectorArguments arguments = new XYTVectorArguments(0, xDelta);

        double [][]prevMatrix = null;
        for (int y = 0; y <= height; y++) {
            routine.checkCanceled();

            double[][] matrix = null;

            arguments.setY(((double)y) / (height + 1));

            matrix = evaluator.calculate(arguments);

            if (prevMatrix == null) {
                prevMatrix = copy(matrix);
                continue;
            }

            for (int i = 0; i < nEq; i++) {
                Equation.Type eqType = equations[i].getType();

                double []row = matrix[i];
                double []prevRow = prevMatrix[i];

                locusData[i][y - 1] = eqType
                        .accept(new LocusRowDiffVisitor(row, prevRow));
            }

            prevMatrix = copy(matrix);
        }

        for (int i = 0; i < nEq; i++) {
            ret[i] = new DiscreteLocus(locusData[i]);
        }

        return ret;
    }

    private static double[][] copy(double[][] input) {
        double [][]ret = new double[input.length][];
        for (int i = 0; i < input.length; i++) {
            double[] values = input[i];
            ret[i] = Arrays.copyOf(values, values.length);
        }
        return ret;
    }

    private class XYTVectorArguments implements VectorArguments {
        private final ConstantVectorFiller yFiller;
        private final ConstantVectorFiller tFiller;
        private final LinearVectorFiller xFiller;

        public XYTVectorArguments(double xStart, double xDelta) {
            yFiller = new ConstantVectorFiller(0);
            tFiller = new ConstantVectorFiller(0);
            xFiller = new LinearVectorFiller(xStart, xDelta);
        }

        @Override
        public String[] getArgumentNames() {
            return new String[] { "x", "y", "t" };
        }

        public void setY(double y) {
            yFiller.setValue(y);
        }

        public void setT(double t) {
            tFiller.setValue(t);
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
