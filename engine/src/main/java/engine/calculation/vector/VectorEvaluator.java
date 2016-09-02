package engine.calculation.vector;

import engine.expressions.Calculable;

/**
 * User: Oleksiy Pylypenko
 * At: 3/12/13  6:28 PM
 */
public interface VectorEvaluator {
    void setCalculables(Calculable[] calculables);

    void setSize(int size);

    void setTimeReporter(TimeReporter timeReporter);

    void prepare();

    double[][] calculate(VectorArguments arguments);

    void clear();
}
