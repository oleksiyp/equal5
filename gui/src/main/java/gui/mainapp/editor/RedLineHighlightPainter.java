package gui.mainapp.editor;

import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * User: Somebody in internet
 * Date: 3/23/13
 * Time: 4:03 PM
 */
public class RedLineHighlightPainter implements Highlighter.HighlightPainter {
    private void paintLine(Graphics g, Rectangle r, int x2) {
        int ytop = r.y + r.height - 3;
        g.fillRect(r.x, ytop, x2 - r.x, 3);
    }

    public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
        Rectangle r0, r1, rbounds = bounds.getBounds();
        int xmax = rbounds.x + rbounds.width;

        try {
            r0 = c.modelToView(p0);
            r1 = c.modelToView(p1);
        } catch (BadLocationException ex) {
            return;
        }

        if (r0 == null || r1 == null) {
            return;
        }

        g.setColor(c.getSelectionColor());

        if (r0.y == r1.y) {
            paintLine(g, r0, r1.x);
            return;
        }

        paintLine(g, r0, xmax);

        r0.y += r0.height;
        r0.x = rbounds.x;
        while (r0.y < r1.y) {
            paintLine(g, r0, xmax);
            r0.y += r0.height;
        }

        paintLine(g, r0, r1.x);
    }
}
