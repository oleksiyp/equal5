package engine.calculation.evaluator;

import engine.calculation.Arguments;
import engine.calculation.functions.FunctionVisitor;
import engine.calculation.functions.*;
import engine.expressions.Calculable;

import java.util.Stack;

class EvaluatingVisitor implements FunctionVisitor {
    private final Arguments arguments;
    private final Stack<Double> stack = new Stack<Double>();

    public EvaluatingVisitor(Arguments arguments) {
        this.arguments = arguments;
    }

    public double calculate(Calculable calculable) {
        calculable.accept(this);
        return stack.pop();
    }

    @Override
    public void visit(Constant constant) {
        stack.push(constant.getValue());
    }

    @Override
    public void visit(Variable variable) {
        String name = variable.getString();
        double value = arguments.getValue(name);
        stack.push(value);
    }

    @Override
    public void visit(Negation negation) {
        double value = calculate(negation.getOperand());
        stack.push(-value);
    }

    @Override
    public void visit(Addition addition) {
        double left = calculate(addition.getLeftSide());
        double right = calculate(addition.getRightSide());

        stack.push(left + right);
    }


    @Override
    public void visit(Subtraction subtraction) {
        double left = calculate(subtraction.getLeftSide());
        double right = calculate(subtraction.getRightSide());

        stack.push(left - right);
    }

    @Override
    public void visit(Division division) {
        double left = calculate(division.getLeftSide());
        double right = calculate(division.getRightSide());

        stack.push(left / right);

    }

    @Override
    public void visit(Multiplication multiplication) {
        double left = calculate(multiplication.getLeftSide());
        double right = calculate(multiplication.getRightSide());

        stack.push(left * right);
    }

    @Override
    public void visit(Power power) {
        double left = calculate(power.getLeftSide());
        double right = calculate(power.getRightSide());

        stack.push(Math.pow(left, right));
    }

    @Override
    public void visit(MathCalculable mathFunction) {
        final Calculable[] args = mathFunction.getArguments();
        mathFunction
                .getType()
                .accept(new MathCalcVisitor(args));
    }

    private class MathCalcVisitor implements MathFunctionTypeVisitor {
        private final Calculable[] args;

        public MathCalcVisitor(Calculable[] args) {
            this.args = args;
        }

        @Override
        public void identity() {
            double x = calculate(args[0]);
            stack.push(x);
        }

        @Override
        public void sin() {
            double x = calculate(args[0]);
            stack.push(Math.sin(x));
        }

        @Override
        public void cos() {
            double x = calculate(args[0]);
            stack.push(Math.sin(x));
        }

        @Override
        public void tan() {
            double x = calculate(args[0]);
            stack.push(Math.tan(x));
        }

        @Override
        public void asin() {
            double x = calculate(args[0]);
            stack.push(Math.asin(x));
        }

        @Override
        public void acos() {
            double x = calculate(args[0]);
            stack.push(Math.acos(x));
        }

        @Override
        public void atan() {
            double x = calculate(args[0]);
            stack.push(Math.atan(x));
        }

        @Override
        public void exp() {
            double x = calculate(args[0]);
            stack.push(Math.exp(x));
        }

        @Override
        public void log() {
            double x = calculate(args[0]);
            stack.push(Math.log(x));
        }

        @Override
        public void sqrt() {
            double x = calculate(args[0]);
            stack.push(Math.log(x));
        }

        @Override
        public void remainder() {
            double x = calculate(args[0]);
            double y = calculate(args[1]);
            stack.push(Math.IEEEremainder(x, y));
        }

        @Override
        public void ceil() {
            double x = calculate(args[0]);
            stack.push(Math.ceil(x));
        }

        @Override
        public void floor() {
            double x = calculate(args[0]);
            stack.push(Math.floor(x));
        }

        @Override
        public void atan2() {
            double x = calculate(args[0]);
            double y = calculate(args[1]);
            stack.push(Math.atan2(x, y));
        }

        @Override
        public void pow() {
            double x = calculate(args[0]);
            double y = calculate(args[1]);
            stack.push(Math.pow(x, y));
        }

        @Override
        public void round() {
            double x = calculate(args[0]);
            stack.push((double) Math.round(x));
        }

        @Override
        public void random() {
            stack.push(Math.random());
        }

        @Override
        public void abs() {
            double x = calculate(args[0]);
            stack.push(Math.abs(x));
        }

        @Override
        public void max() {
            double x = calculate(args[0]);
            double y = calculate(args[1]);
            stack.push(Math.max(x, y));
        }

        @Override
        public void min() {
            double x = calculate(args[0]);
            double y = calculate(args[1]);
            stack.push(Math.min(x, y));
        }

        @Override
        public void signum() {
            double x = calculate(args[0]);
            stack.push(Math.signum(x));
        }

        @Override
        public void sinh() {
            double x = calculate(args[0]);
            stack.push(Math.sinh(x));
        }

        @Override
        public void cosh() {
            double x = calculate(args[0]);
            stack.push(Math.cosh(x));
        }

        @Override
        public void tanh() {
            double x = calculate(args[0]);
            stack.push(Math.tanh(x));
        }

        @Override
        public void hypot() {
            double x = calculate(args[0]);
            double y = calculate(args[0]);
            stack.push(Math.hypot(x, y));
        }

        @Override
        public void expm1() {
            double x = calculate(args[0]);
            stack.push(Math.expm1(x));
        }

        @Override
        public void log1p() {
            double x = calculate(args[0]);
            stack.push(Math.log1p(x));
        }
    }
}