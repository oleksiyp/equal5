package engine.calculation.vector;

import engine.expressions.Name;

/**
 * User: Oleksiy Pylypenko
 * At: 3/12/13  6:30 PM
 */
public interface VectorArguments {
    Name[]getArguments();

    VectorFiller getVectorFiller(Name argument);
}
