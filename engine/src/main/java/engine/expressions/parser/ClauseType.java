package engine.expressions.parser;

/**
* User: Oleksiy Pylypenko
* Date: 3/5/13
* Time: 3:42 PM
*/
public enum ClauseType {
    EQUATIONS,
    EQUATION, // T
    EXPRESSION, // T
    TERM, // T
    FACTOR, //
    PARENTHESES, // T
    CONSTANT, // T
    VARIABLE, // T
    DECIMAL_FLOAT, // T
    EXPONENT, // T
    ARGUMENTS, // T
    MATH_FUNCTION, // T
    DIGIT // T
}
