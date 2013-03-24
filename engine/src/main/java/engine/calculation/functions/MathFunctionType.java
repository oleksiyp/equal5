package engine.calculation.functions;

/**
* User: Oleksiy Pylypenko
* Date: 3/23/13
* Time: 1:55 PM
*/
public enum MathFunctionType {
    SIN("sin", 1),
    COS("cos", 1),
    SIGNUM("sign", 1),
    IDENTITY("id", 1);
    // Math functions:
    // sin, cos, tan, asin, acos, atan,
    // toRadians, toDegrees, exp, log, log10, sqrt, cbrt
    // IEEEremainder, ceil, floor, rint,
    // atan2, pow, round, random, abs, max,
    // min, ulp, signum, sinh, cosh, tanh
    // hypot, expm1, log1p, scalb,

    private final String inExpressionName;
    private final int argumentsCount;

    MathFunctionType(String inExpressionName, int argumentsCount) {
        this.inExpressionName = inExpressionName;
        this.argumentsCount = argumentsCount;
    }

    public void accept(MathFunctionTypeVisitor visitor) {
        switch (this) {
            case SIN: visitor.sin(); return;
            case COS: visitor.cos(); return;
            case SIGNUM: visitor.signum(); return;
            case IDENTITY: visitor.identity(); return;
        }
        throw new UnsupportedOperationException("accept");
    }

    public String getInExpressionName() {
        return inExpressionName;
    }

    public int getArgumentsCount() {
        return argumentsCount;
    }

    public static MathFunctionType bySignature(String name, int argsCount) {
        for (MathFunctionType type : MathFunctionType.values()) {
            if (type.getInExpressionName().equals(name)
                    && type.getArgumentsCount() == argsCount) {
                return type;
            }
        }
        return null;
    }
}
