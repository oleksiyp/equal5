package engine.calculation.vector;

import java.util.concurrent.ExecutorService;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/13/13
 * Time: 8:47 AM
 */
public interface ConcurrentVectorEvaluator extends VectorEvaluator {
    void setConcurrentEvaluation(int concurrency, ExecutorService service);
}
