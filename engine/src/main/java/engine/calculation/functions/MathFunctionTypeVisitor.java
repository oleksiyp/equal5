package engine.calculation.functions;

/**
* User: Oleksiy Pylypenko
* Date: 3/23/13
* Time: 1:56 PM
*/
public interface MathFunctionTypeVisitor {
    void sin();

    void cos();

    void signum();

    void identity();
}
