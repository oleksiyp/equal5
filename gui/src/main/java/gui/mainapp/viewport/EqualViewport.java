package gui.mainapp.viewport;

import engine.calculation.CalculationParameters;
import engine.calculation.CalculationResults;
import engine.calculation.ViewportBounds;
import engine.calculation.ViewportSize;
import engine.expressions.Equation;
import engine.expressions.parser.ClauseType;
import engine.expressions.parser.ExpressionParser;
import engine.expressions.parser.ParsingException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ThreadFactory;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/14/13
 * Time: 9:06 PM
 */
public class EqualViewport extends JComponent  {
    public static final ClauseType TOP_CLAUSE = ClauseType.EQUATIONS;

    private Equation[] equations;
    private ViewportBounds viewportBounds;
    private double t;

    private ViewportUpdater updater;

    private ExpressionParser parser;
    private volatile boolean recalculateEachSubmit;
    private volatile boolean delayedRecalculation;

    private final CoordinateSystem coordinateSystem;

    public EqualViewport() {
        updater = new ViewportUpdater(new SomeThreadFactory());

        coordinateSystem = new CoordinateSystem();

        updater.addFrameListener(new RepaintFrameListener());
        updater.start();

        coordinateSystem
                .getOptions()
                .addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        repaint();
                    }
                });
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
        ViewportSize size = getViewportSize();
        updater.setParameters(
                new CalculationParameters(viewportBounds,
                        size, t, equations),
                recalculateEachSubmit,
                delayedRecalculation);
    }

    private ViewportSize getViewportSize() {
        int width = getWidth();
        int height = getHeight();
        if (width < 0 || height < 0) {
            width = height = 0;
        }
        return new ViewportSize(width, height);
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

    public CoordinateSystem getCoordinateSystem() {
        return coordinateSystem;
    }

    public void setExpression(String expression) throws ParsingException {
        if (parser == null) {
            return;
        }
        equations = (Equation[]) parser.parse(TOP_CLAUSE, expression);
        submitCalculation();
    }

    public void setParser(ExpressionParser parser) {
        this.parser = parser;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        ViewportSize size = getViewportSize();
        if (viewportBounds != null && !size.isEmpty()) {
            coordinateSystem.draw(g, size, viewportBounds);
        }
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
}
