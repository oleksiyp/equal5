package engine.calculation;

import engine.calculation.functions.Subtraction;
import engine.calculation.vector.VectorArguments;
import engine.calculation.vector.VectorEvaluator;
import engine.calculation.vector.VectorMachineEvaluator;
import engine.expressions.Equation;
import engine.expressions.Function;
import engine.locus.DiscreteLocus;
import engine.locus.PixelDrawable;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/13/13
 * Time: 11:26 AM
 */
public class VectorCalculationEngine implements CalculationEngine {
    @Override
    public PixelDrawable[] calculate(int width, int height, Equation[] equations) {
        int nEq = equations.length;

        PixelDrawable []result = new PixelDrawable[nEq];
        Function[] multiFunction = new Function[nEq];

        for (int i = 0; i < nEq; i++)
        {
            Equation equation = equations[i];
            multiFunction[i] = new Subtraction(equation.getLeftPart(),
                    equation.getRightPart());
        }

        VectorEvaluator evaluator = new VectorMachineEvaluator();
        evaluator.setSize(width + 1);
        evaluator.setFunctions(multiFunction);

        int [][][]locusData = new int[nEq][height][];
        double [][]prevValues;

//        VectorArguments arguments = new XYRowArguments(coords);

        for (int y = 0; y <= height; y++) {
//            coords[1] = ((double)y) / (height + 1);
//            for (int x = 0; x <= width; x++) {
//                coords[0] = ((double)x) / (width + 1);
//                    row[x] = evaluator.calculate(diff, arguments);
//            }

//            double[][] results = evaluator.calculate(arguments);

            if (y == 0) {
                continue;
            }

            for (int i = 0; i < nEq; i++) {
//            locusData[y - 1] = equation
//                    .getType()
//                    .accept(new RowDifferentiator(row, prevRow));

            }
        }

//        result[i] = new DiscreteLocus(locusData);

        return result;
    }
}
