package engine.calculation.functions;

/**
* User: Oleksiy Pylypenko
* Date: 3/23/13
* Time: 1:55 PM
*/
public enum MathFunctionType {
    SIN("sin", 1),
    COS("cos", 1),
    SIGNUM("sign", 1);

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
        }
        throw new UnsupportedOperationException("accept");
    }

    public String getInExpressionName() {
        return inExpressionName;
    }

    public int getArgumentsCount() {
        return argumentsCount;
    }
}
