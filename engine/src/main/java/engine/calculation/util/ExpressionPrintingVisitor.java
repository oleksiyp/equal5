package engine.calculation.util;

import engine.calculation.FunctionVisitor;
import engine.calculation.functions.*;

import java.util.Stack;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 11:49 PM
 */
public class ExpressionPrintingVisitor implements FunctionVisitor {
    private final ExpressionWriter writer;
    private final Stack<Boolean> brackets = new Stack<Boolean>();

    private int priority = 0;


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


    private void visitBinaryOp(BinaryOperator operator) {
        openBracket(operator.getPriority());
        operator.getLeftSide().accept(this);
        writer.write(operator.getSymbolicRepresentation());
        operator.getRightSide().accept(this);
        closeBracket(operator.getPriority());
    }

    private void openBracket(int priority) {
        boolean needOutput = changePriority(priority);
        brackets.push(needOutput);
        if (needOutput) {
            writer.outputOpenBracket();
        }
    }

    private boolean changePriority(int priority) {
        boolean ret = priority < this.priority;
        this.priority = priority;
        return ret;
    }

    private void closeBracket(int priority) {
        changePriority(priority);
        if (brackets.pop()) {
            writer.outputCloseBracket();
        }
    }

}
