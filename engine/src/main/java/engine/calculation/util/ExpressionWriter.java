package engine.calculation.util;

import engine.calculation.functions.BinaryOperator;

import java.io.PrintWriter;

/**
 * User: Oleksiy Pylypenko
 * Date: 2/9/13
 * Time: 11:49 PM
 */
public class ExpressionWriter {
    private final PrintWriter writer;

    public ExpressionWriter(PrintWriter writer) {
        this.writer = writer;
    }

    public void write(double value) {
        writer.print(value);
    }

    public void write(String text) {
        writer.write(text);
    }

    public void writeSpaces(int count) {
        for (int i = 0; i < count; i++) writer.print(' ');
    }

    public void outputOpenBracket() {
        writer.write("(");
    }

    public void outputCloseBracket() {
        writer.write(")");
    }
}
