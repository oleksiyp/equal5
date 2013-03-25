package engine.calculation.util;

import engine.calculation.functions.FunctionVisitor;
import engine.calculation.functions.*;
import engine.expressions.Function;

import java.util.Stack;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 11:49 PM
 */
public class ExpressionPrintingVisitor implements FunctionVisitor {
    private final ExpressionWriter writer;
    private final Stack<Integer> priorities = new Stack<Integer>();
    private final Stack<Boolean> brackets = new Stack<Boolean>();

    public ExpressionPrintingVisitor(ExpressionWriter writer) {
        this.writer = writer;
    }

    @Override
    public void visit(Constant constant) {
        writer.write(constant.getValue());
    }

    @Override
    public void visit(Variable variable) {
        writer.write(variable.getString());
    }

    @Override
    public void visit(Addition addition) {
        visitBinaryOp(addition);
    }

    @Override
    public void visit(Subtraction subtraction) {
        visitBinaryOp(subtraction);
    }

    @Override
    public void visit(Multiplication multiplication) {
        visitBinaryOp(multiplication);
    }

    @Override
    public void visit(Division division) {
        visitBinaryOp(division);
    }

    @Override
    public void visit(Power power) {
        visitBinaryOp(power);
    }

    @Override
    public void visit(final MathFunction mathFunction) {
        writer.write(mathFunction.getType().getInExpressionName());
        writeArguments(mathFunction.getArguments());
    }

    private void writeArguments(Function[] arguments) {
        writer.write("(");
        for (int i = 0; i < arguments.length; i++) {
            arguments[i].accept(ExpressionPrintingVisitor.this);
            if (i > 0) {
                writer.write(", ");
            }
        }
        writer.write(")");
    }


    private void visitBinaryOp(BinaryOperator operator) {
        openBracket(operator.getPriority());
        operator.getLeftSide().accept(this);
        writer.write(operator.getSymbolicRepresentation());
        operator.getRightSide().accept(this);
        closeBracket(operator.getPriority());
    }

    private void openBracket(int priority) {
        boolean needOutput = !priorities.isEmpty()
                && priorities.peek() > priority;
        brackets.push(needOutput);
        priorities.push(priority);
        if (needOutput) {
            writer.outputOpenBracket();
        }
    }

    private void closeBracket(int priority) {
        priorities.pop();
        if (brackets.pop()) {
            writer.outputCloseBracket();
        }
    }
}
