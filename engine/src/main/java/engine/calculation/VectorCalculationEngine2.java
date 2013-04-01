package engine.calculation;

import engine.calculation.drawables.Drawable;
import engine.calculation.drawables.matrix.MatrixDrawable;
import engine.calculation.functions.MathFunction;
import engine.calculation.functions.MathFunctionType;
import engine.calculation.functions.Subtraction;
import engine.calculation.vector.VectorArguments;
import engine.calculation.vector.VectorEvaluator;
import engine.calculation.vector.fillers.ConstantVectorFiller;
import engine.calculation.vector.fillers.VectorFiller;
import engine.expressions.Equation;
import engine.expressions.Function;
import engine.calculation.drawables.locus.DiscreteLocus;
import util.Cancelable;
import util.CancellationRoutine;

import java.util.Arrays;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/27/13
 * Time: 11:29 PM
 */
public class VectorCalculationEngine2 implements CalculationEngine, Cancelable {
    private final VectorEvaluator evaluator;
    private CancellationRoutine routine = CancellationRoutine.NO_ROUTINE;

    public VectorCalculationEngine2(VectorEvaluator evaluator) {
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

        evaluator.setSize((width+1)*(height+1));
        evaluator.setFunctions(multiFunction);

        evaluator.prepare();

        ViewportBounds bounds = parameters.getBounds();

        double t = parameters.getT();

        double xDelta = bounds.getXDelta(width);
        double yDelta = bounds.getYDelta(height);

        double xStart = bounds.getLeft() - xDelta / 2.0;
        double yStart = bounds.getTop() - yDelta / 2.0;

        AdvancedXYTVectorArguments args = new AdvancedXYTVectorArguments(
                xStart, xDelta,
                yStart, yDelta,
                width + 1, height+1,
                t);

        double[][] matrix = evaluator.calculate(args);
        int [][][]locusData = new int[nEq][][];

        for (int i = 0; i < nEq; i++) {
            Equation.Type eqType = equations[i].getType();

            locusData[i] = eqType
                    .accept(new LocusMatrixDiffVisitor(matrix[i], width + 1));
        }

        for (int i = 0; i < nEq; i++) {
            ret[i] = new DiscreteLocus(locusData[i]);
        }

        return new CalculationResults(parameters, ret);
    }

    private class AdvancedXYTVectorArguments implements VectorArguments {
        private final double xStart;
        private final double xDelta;
        private final double yStart;
        private final double yDelta;
        private final int w;
        private final int h;
        private final ConstantVectorFiller tFiller;

        private final XFiller xFiller;
        private final YFiller yFiller;

        public AdvancedXYTVectorArguments(
                double xStart, double xDelta,
                double yStart, double yDelta,
                int w, int h,
                double tFiller)
        {
            this.xStart = xStart;
            this.xDelta = xDelta;
            this.yStart = yStart;
            this.yDelta = yDelta;
            this.w = w;
            this.h = h;
            this.tFiller = new ConstantVectorFiller(tFiller);
            xFiller = new XFiller();
            yFiller = new YFiller();
        }

        class XFiller implements VectorFiller {
            @Override
            public void fill(double[] vector) {
                int xoff = 0;

                double xs = xStart;
                double xd = xDelta;
                int len = vector.length;
                int width = w;

                for (int i = 0; i < len; i++) {
                    xoff++;
                    if (xoff == width) {
                        xoff = 0;
                    }
                    vector[i] = xs + xoff * xd;
                }
            }
        }
        class YFiller implements VectorFiller {
            @Override
            public void fill(double[] vector) {
                int yoff = 0;
                int xoff = 0;

                double ys = yStart;
                double yd = yDelta;
                int len = vector.length;
                int width = w;

                double y = ys;
                for (int i = 0; i < len; i++) {
                    xoff++;
                    if (xoff == width) {
                        xoff = 0;
                        yoff++;
                        y = ys + yoff * yd;
                    }
                    vector[i] = y;
                }
            }
        }

        @Override
        public String[] getArgumentNames() {
            return new String[] { "x", "y", "t" };
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
            throw new UnsupportedOperationException("getVectorFiller");
        }
    }
}
