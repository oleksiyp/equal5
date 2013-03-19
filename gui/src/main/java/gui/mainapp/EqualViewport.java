package gui.mainapp;

import engine.calculation.tasks.CalculationParameters;
import engine.calculation.tasks.ViewportBounds;
import engine.expressions.Equation;
import engine.expressions.ExpressionParser;
import engine.expressions.ParsingException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.ThreadFactory;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/14/13
 * Time: 9:06 PM
 */
public class EqualViewport extends JPanel  {
    private Equation equation;
    private ViewportBounds viewportBounds;

    private ViewportUpdater updater;

    private ExpressionParser parser;

    public EqualViewport() {
        super(true);

        updater = new ViewportUpdater(new SomeThreadFactory(),
                new RepaintViewportChangedListener());

        updater.start();

        addComponentListener(new RecalcOnResizeListener());
    }

    public void setViewportBounds(ViewportBounds viewportBounds) {
        this.viewportBounds = viewportBounds;
        submitRecalc();
    }

    private void submitRecalc() {
        if (equation == null || viewportBounds == null) {
            return;
        }
        updater.setParameters(
                new CalculationParameters(equation,
                        viewportBounds,
                        getWidth(),
                        getHeight())
        );
    }

    public void setExpression(String expression) throws ParsingException {
        if (parser == null) {
            return;
        }
        equation = parser.parseEquation(expression);
        submitRecalc();
    }

    public void setParser(ExpressionParser parser) {
        this.parser = parser;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        updater.paint(g, width, height);
    }

    private static class SomeThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r);
        }
    }

    private class RepaintViewportChangedListener implements ViewportChangedListener {
        @Override
        public void viewportChanged() {
            repaint();
        }
    }

    private class RecalcOnResizeListener extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            submitRecalc();
        }
    }
}
