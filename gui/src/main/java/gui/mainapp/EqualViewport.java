package gui.mainapp;

import engine.calculation.CalculationEngine;
import engine.expressions.Equation;
import engine.expressions.ExpressionParser;
import engine.expressions.ParsingException;
import engine.locus.DrawToImage;
import engine.locus.PixelDrawable;
import engine.locus.RectRange;

import javax.swing.*;
import java.awt.*;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/14/13
 * Time: 9:06 PM
 */
public class EqualViewport extends JPanel {
    private Equation equation;
    private ViewportBounds viewportBounds;

    private CalculationEngine engine;
    private ExpressionParser parser;

    public EqualViewport() {
        super(true);
    }

    public ViewportBounds getViewportBounds() {
        return viewportBounds;
    }

    public void setViewportBounds(ViewportBounds viewportBounds) {
        this.viewportBounds = viewportBounds;
        repaint();
    }

    public void setExpression(String expression) throws ParsingException {
        if (parser == null) {
            return;
        }
        equation = parser.parseEquation(expression);
        repaint();
    }

    public void setEngine(CalculationEngine engine) {
        this.engine = engine;
    }

    public void setParser(ExpressionParser parser) {
        this.parser = parser;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (equation == null || engine == null || viewportBounds == null) {
            return;
        }
        int width = getWidth();
        int height = getHeight();
        PixelDrawable[] drawables = engine.calculate(width, height,
                new Equation[]{equation});
        PixelDrawable drawable = drawables[0];

        RectRange sz = drawable.getSize();
        DrawToImage drawToImage = new DrawToImage(sz);
        drawable.draw(sz, drawToImage);

        g.drawImage(drawToImage.getImage(), 0, 0, null);
    }
}
