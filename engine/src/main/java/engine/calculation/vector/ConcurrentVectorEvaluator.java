package engine.calculation.vector;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/13/13
 * Time: 8:47 AM
 */
public interface ConcurrentVectorEvaluator extends VectorEvaluator {
    void setConcurrency(Integer value);
}
