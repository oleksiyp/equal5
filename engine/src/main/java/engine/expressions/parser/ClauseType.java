package engine.expressions.parser;

/**
* User: Oleksiy Pylypenko
* Date: 3/5/13
* Time: 3:42 PM
*/
public enum ClauseType {
    EQUATIONS,
    EQUATION, // T
    ADDITIVE_EXPRESSION, // T
    MULTIPLICATIVE_EXPRESSION, // T
    PRIMARY_EXPRESSION, //
    MATH_FUNCTION, // T
    ARGUMENTS, // T
    PARENTHESES, // T
    VARIABLE, // T
    CONSTANT; // T

    public <T> T accept(ClauseTypeVisitor<T> visitor) throws Exception {
        switch (this) {
            case EQUATIONS: return visitor.equations();
            case EQUATION: return visitor.equation();
            case ADDITIVE_EXPRESSION: return visitor.additiveExpression();
            case MULTIPLICATIVE_EXPRESSION: return visitor.multiplicativeExpression();
            case PRIMARY_EXPRESSION: return visitor.primaryExpression();
            case PARENTHESES: return visitor.parentheses();
            case CONSTANT: return visitor.constant();
            case VARIABLE: return visitor.variable();
            case ARGUMENTS: return visitor.arguments();
            case MATH_FUNCTION: return visitor.mathFunction();
        }
        throw new UnsupportedOperationException(this + ".accept(" + visitor + ")");
    }
}
