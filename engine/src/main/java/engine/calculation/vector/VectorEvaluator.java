package engine.calculation.vector;

import engine.expressions.Function;

/**
 * User: Oleksiy Pylypenko
 * At: 3/12/13  6:28 PM
 */
public interface VectorEvaluator {
    void setFunctions(Function[] functions);

    void setSize(int size);

    void setTimeReporter(TimeReporter timeReporter);

    void prepare();

    double[][] calculate(VectorArguments arguments);

    void clear();
}
