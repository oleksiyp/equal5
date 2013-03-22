package gui.mainapp;

import engine.calculation.CalculationParameters;
import engine.calculation.ViewportBounds;
import engine.calculation.ViewportSize;
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
public class EqualViewport extends JComponent  {
    private Equation equation;
    private ViewportBounds viewportBounds;
    private double t;

    private ViewportUpdater updater;

    private ExpressionParser parser;

    public EqualViewport() {
            updater = new ViewportUpdater(new SomeThreadFactory(),
                new RepaintViewportChangedListener());

        updater.start();

        addComponentListener(new RecalcOnResizeListener());
    }

    public void setViewportBounds(ViewportBounds viewportBounds) {
        this.viewportBounds = viewportBounds;
        submitRecalc();
    }

    public void setT(double t) {
        this.t = t;
        submitRecalc();
    }

    private void submitRecalc() {
        if (equation == null || viewportBounds == null) {
            return;
        }
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }
        ViewportSize size = new ViewportSize(width, height);
        updater.setParameters(
                new CalculationParameters(viewportBounds,
                        size, t, equation)
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

    private class RepaintViewportChangedListener implements ViewportChangedListener, Runnable {
        @Override
        public void viewportChanged() {
            SwingUtilities.invokeLater(this);
        }

        @Override
        public void run() {
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
