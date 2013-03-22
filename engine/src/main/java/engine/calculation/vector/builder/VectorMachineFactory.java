package engine.calculation.vector.builder;

import engine.calculation.vector.VectorMachine;
import engine.expressions.Function;

/**
 * User: Oleksiy Pylypenko
 * At: 3/22/13  1:47 PM
 */
public interface VectorMachineFactory {
    VectorMachine create(Function[] functions);
}
