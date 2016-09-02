package engine.calculation.functions;

import engine.calculation.Arguments;
import engine.calculation.evaluator.ImmediateFunctionEvaluator;
import engine.calculation.util.ExpressionPrintingVisitor;
import engine.calculation.util.ExpressionWriter;
import engine.expressions.Calculable;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 11:51 PM
 */
public abstract class AbstractCalculable implements Calculable {
    @Override
    public String toString() {
        return toString(this);
    }

    public double eval(Arguments arguments) {
        ImmediateFunctionEvaluator eval = new ImmediateFunctionEvaluator();
        return eval.calculate(this, arguments);
    }

    private static String toString(Calculable calculable) {
        StringWriter strWriter = new StringWriter();
        PrintWriter prnWriter = new PrintWriter(strWriter);
        ExpressionWriter exprWriter = new ExpressionWriter(prnWriter);
        ExpressionPrintingVisitor visitor = new ExpressionPrintingVisitor(exprWriter);
        calculable.accept(visitor);
        prnWriter.flush();
        return strWriter.toString();

    }
}
