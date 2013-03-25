package engine.calculation.functions;

/**
* User: Oleksiy Pylypenko
* Date: 3/23/13
* Time: 1:56 PM
*/
public interface MathFunctionTypeVisitor {
    void sin();

    void cos();

    void identity();

    void tan();

    void asin();

    void acos();

    void atan();

    void exp();

    void log();

    void sqrt();

    void remainder();

    void ceil();

    void floor();

    void atan2();

    void pow();

    void round();

    void random();

    void abs();

    void signum();

    void max();

    void min();

    void sinh();

    void cosh();

    void tanh();

    void hypot();

    void expm1();

    void log1p();
}
