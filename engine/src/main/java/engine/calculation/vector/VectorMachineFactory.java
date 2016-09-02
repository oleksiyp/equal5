package engine.calculation.vector;

import engine.expressions.Calculable;

/**
 * User: Oleksiy Pylypenko
 * At: 3/22/13  1:47 PM
 */
public interface VectorMachineFactory {
    VectorMachine create(Calculable[] calculables);
}
