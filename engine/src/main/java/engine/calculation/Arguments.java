package engine.calculation;

import engine.expressions.Name;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:38 PM
 */
public interface Arguments {
    Name []getArguments();

    double getValue(Name name);
}
