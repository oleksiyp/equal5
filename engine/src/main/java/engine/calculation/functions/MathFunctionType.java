package engine.calculation.functions;

/**
* User: Oleksiy Pylypenko
* Date: 3/23/13
* Time: 1:55 PM
*/
public enum MathFunctionType {
    IDENTITY("id", 1, "id(x)"),
    SIN("sin", 1, "sin(x)"),
    COS("cos", 1, "cos(x)"),
    TAN("tan", 1, "tan(x)"),
    ASIN("asin", 1, "asin(x)"),
    ACOS("acos", 1, "acos(x)"),
    ATAN("atan", 1, "atan(x)"),
    EXP("exp", 1, "exp(x)"),
    LOG("log", 1, "log(x)"),
    SQRT("sqrt", 1, "sqrt(x)"),
    REMAINDER("remainder", 2, "remainder(quotient, divisor)"),
    CEIL("ceil", 1, "ceil(x)"),
    FLOOR("floor", 1, "floor(x)"),
    ATAN2("atan", 2, "atan(y, x)"),
    POW("pow", 2, "pow(x, degree)"),
    ROUND("round", 1, "round(x)"),
    RANDOM("random", 0, "random()"),
    ABS("abs", 1, "abs(x)"),
    MAX("max", 2, "max(a,b)"),
    MIN("min", 2, "min(a,b)"),
    SIGNUM("sign", 1, "sign(x)"),
    SINH("sinh", 1, "sinh(x)"),
    COSH("cosh", 1, "cosh(x)"),
    TANH("tanh", 1, "tanh(x)"),
    HYPOT("hypot", 2, "hypot(c1, c2)"),
    EXPM1("expm1", 1, "expm1(1)"),
    LOG1P("log1p", 1, "log1p(1)");

    // Math functions:
    // sin, cos, tan, asin, acos, atan,
    // toRadians, toDegrees, exp, log, log10, sqrt, cbrt
    // IEEEremainder, ceil, floor, rint,
    // atan2, pow, round, random, abs, max,
    // min, ulp, signum, sinh, cosh, tanh
    // hypot, expm1, log1p, scalb,

    private final String inExpressionName;
    private final int argumentsCount;
    private final String signature;
    private boolean withSideEffects = false;
    static {
        RANDOM.withSideEffects = true;
    }

    MathFunctionType(String inExpressionName, int argumentsCount, String signature) {
        this.inExpressionName = inExpressionName;
        this.argumentsCount = argumentsCount;
        this.signature = signature;
    }

    public void accept(MathFunctionTypeVisitor visitor) {
        switch (this) {
            case IDENTITY: visitor.identity(); return;
            case SIN: visitor.sin(); return;
            case COS: visitor.cos(); return;
            case TAN: visitor.tan(); return;
            case ASIN: visitor.asin(); return;
            case ACOS: visitor.acos(); return;
            case ATAN: visitor.atan(); return;
            case EXP: visitor.exp(); return;
            case LOG: visitor.log(); return;
            case SQRT: visitor.sqrt(); return;
            case REMAINDER: visitor.remainder(); return;
            case CEIL: visitor.ceil(); return;
            case FLOOR: visitor.floor(); return;
            case ATAN2: visitor.atan2(); return;
            case POW: visitor.pow(); return;
            case ROUND: visitor.round(); return;
            case RANDOM: visitor.random(); return;
            case ABS: visitor.abs(); return;
            case MAX: visitor.max(); return;
            case MIN: visitor.min(); return;
            case SIGNUM: visitor.signum(); return;
            case SINH: visitor.sinh(); return;
            case COSH: visitor.cosh(); return;
            case TANH: visitor.tanh(); return;
            case HYPOT: visitor.hypot(); return;
            case EXPM1: visitor.expm1(); return;
            case LOG1P: visitor.log1p(); return;
        }
        throw new UnsupportedOperationException("accept");
    }

    public String getInExpressionName() {
        return inExpressionName;
    }

    public int getArgumentsCount() {
        return argumentsCount;
    }

    public boolean isWithSideEffects() {
        return withSideEffects;
    }

    public String getSignature() {
        return signature;
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
