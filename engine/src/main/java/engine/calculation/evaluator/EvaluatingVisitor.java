package engine.calculation.evaluator;

import engine.calculation.Arguments;
import engine.calculation.functions.FunctionVisitor;
import engine.calculation.functions.*;
import engine.expressions.Function;

import java.util.Stack;

class EvaluatingVisitor implements FunctionVisitor {
    private final Arguments arguments;
    private final Stack<Double> stack = new Stack<Double>();

    public EvaluatingVisitor(Arguments arguments) {
        this.arguments = arguments;
    }

    public double calculate(Function function) {
        function.accept(this);
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
    public void visit(MathFunction mathFunction) {
        final Function[] args = mathFunction.getArguments();
        mathFunction
                .getType()
                .accept(new MathFunctionCalcVisitor(args));
    }

    private class MathFunctionCalcVisitor implements MathFunctionTypeVisitor {
        private final Function[] args;

        public MathFunctionCalcVisitor(Function[] args) {
            this.args = args;
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
        public void signum() {
            double x = calculate(args[0]);
            stack.push(Math.signum(x));
        }

        @Override
        public void identity() {
            double x = calculate(args[0]);
            stack.push(x);
        }
    }
}