package engine.calculation.functions;

import engine.calculation.Arguments;
import engine.calculation.evaluator.ImmediateFunctionEvaluator;
import engine.calculation.util.ExpressionPrintingVisitor;
import engine.calculation.util.ExpressionWriter;
import engine.expressions.Function;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 11:51 PM
 */
public abstract class AbstractFunction implements Function {
    @Override
    public String toString() {
        return toString(this);
    }

    public double eval(Arguments arguments) {
        ImmediateFunctionEvaluator eval = new ImmediateFunctionEvaluator();
        return eval.calculate(this, arguments);
    }

    private static String toString(Function function) {
        StringWriter strWriter = new StringWriter();
        PrintWriter prnWriter = new PrintWriter(strWriter);
        ExpressionWriter exprWriter = new ExpressionWriter(prnWriter);
        ExpressionPrintingVisitor visitor = new ExpressionPrintingVisitor(exprWriter);
        function.accept(visitor);
        prnWriter.flush();
        return strWriter.toString();

    }
}
