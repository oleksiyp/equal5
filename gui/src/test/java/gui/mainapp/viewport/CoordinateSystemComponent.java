package gui.mainapp.viewport;

import engine.calculation.ViewportBounds;
import engine.calculation.ViewportSize;

import javax.swing.*;
import java.awt.*;

/**
 * User: Oleksiy Pylypenko
 * Date: 4/15/13
 * Time: 12:31 AM
 */
public class CoordinateSystemComponent extends JComponent {
    private final CoordinateSystem system = new CoordinateSystem();
    private final ViewportBounds bounds = new ViewportBounds(-10, 10, -10, 10);

    public CoordinateSystemComponent() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
   	    g.fillRect(0, 0, getWidth(), getHeight());
        Dimension sz = getSize();
        if (sz.width <= 0 || sz.height <= 0) return;
        ViewportSize vpSize = new ViewportSize(sz.width, sz.height);
        system.draw(g, vpSize, bounds);
    }
}
