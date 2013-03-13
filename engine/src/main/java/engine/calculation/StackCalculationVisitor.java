package engine.calculation;

import engine.calculation.functions.*;
import engine.expressions.Function;
import engine.expressions.Name;

import java.util.Stack;

class StackCalculationVisitor implements FunctionVisitor {
    private Arguments arguments;
    final Stack<Double> stack = new Stack<Double>();

    public StackCalculationVisitor(Arguments arguments) {
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
        Name name = variable.getName();
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
}