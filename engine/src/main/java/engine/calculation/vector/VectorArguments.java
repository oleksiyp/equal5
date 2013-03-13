package engine.calculation.vector;


/**
 * User: Oleksiy Pylypenko
 * At: 3/12/13  6:30 PM
 */
public interface VectorArguments {
    String[]getArguments();

    VectorFiller getVectorFiller(String argument);
}
