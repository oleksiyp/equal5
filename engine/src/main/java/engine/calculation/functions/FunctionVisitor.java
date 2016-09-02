package engine.calculation.functions;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  3:19 PM
 */
public interface FunctionVisitor {
    void visit(Constant constant);

    void visit(Variable variable);

    void visit(Negation negation);

    void visit(Addition addition);

    void visit(Subtraction subtraction);

    void visit(Multiplication multiplication);

    void visit(Division division);

    void visit(Power power);

    void visit(MathCalculable mathFunction);
}
