package gui.mainapp.viewport;

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
    private Equation[] equations;
    private ViewportBounds viewportBounds;
    private double t;

    private ViewportUpdater updater;

    private ExpressionParser parser;
    private volatile boolean recalculateEachSubmit;
    private volatile boolean delayedRecalculation;

    public EqualViewport() {
        updater = new ViewportUpdater(new SomeThreadFactory());

        updater.addFrameListener(new RepaintFrameListener());
        updater.start();

        addComponentListener(new RecalcOnResizeListener());
    }

    public void setViewportBounds(ViewportBounds viewportBounds) {
        this.viewportBounds = viewportBounds;
        submitCalculation();
    }

    public void setT(double t) {
        this.t = t;
        submitCalculation();
    }

    public void submitCalculation() {
        if (equations == null || viewportBounds == null) {
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
                        size, t, equations),
                recalculateEachSubmit,
                delayedRecalculation);
    }

    public boolean isRecalculateEachSubmit() {
        return recalculateEachSubmit;
    }

    public void setRecalculateEachSubmit(boolean recalculateEachSubmit) {
        this.recalculateEachSubmit = recalculateEachSubmit;
    }

    public boolean isDelayedRecalculation() {
        return delayedRecalculation;
    }

    public void setDelayedRecalculation(boolean delayedRecalculation) {
        this.delayedRecalculation = delayedRecalculation;
    }

    public void setExpression(String expression) throws ParsingException {
        if (parser == null) {
            return;
        }
        equations = parser.parseEquations(expression);
        submitCalculation();
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

    public void addFrameListener(FrameListener listener) {
        updater.addFrameListener(listener);
    }

    public void removeFrameListener(FrameListener listener) {
        updater.removeFrameListener(listener);
    }

    private static class SomeThreadFactory implements ThreadFactory {
        int n = 1;
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("EqualViewportThread #" + n++);
            return thread;
        }
    }

    private class RepaintFrameListener implements FrameListener {
        @Override
        public void frameDone() {
            repaint();
        }
    }

    private class RecalcOnResizeListener extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            submitCalculation();
        }
    }
}
