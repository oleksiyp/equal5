package engine.calculation.functions;

/**
* User: Oleksiy Pylypenko
* Date: 3/23/13
* Time: 1:55 PM
*/
public enum MathFunctionType {
    IDENTITY("id", 1),
    SIN("sin", 1),
    COS("cos", 1),
    TAN("tan", 1),
    ASIN("asin", 1),
    ACOS("acos", 1),
    ATAN("atan", 1),
    EXP("exp", 1),
    LOG("log", 1),
    SQRT("sqrt", 1),
    REMAINDER("remainder", 2),
    CEIL("ceil", 1),
    FLOOR("floor", 1),
    ATAN2("atan", 2),
    POW("pow", 2),
    ROUND("round", 1),
    RANDOM("random", 0),
    ABS("abs", 1),
    MAX("max", 2),
    MIN("min", 2),
    SIGNUM("sign", 1),
    SINH("sinh", 1),
    COSH("cosh", 1),
    TANH("tanh", 1),
    HYPOT("hypot", 2),
    EXPM1("expm1", 1),
    LOG1P("log1p", 1);

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
