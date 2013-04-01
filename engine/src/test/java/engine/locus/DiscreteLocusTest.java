package engine.locus;

import engine.calculation.drawables.PixelDrawer;
import engine.calculation.drawables.RectRange;
import engine.calculation.drawables.locus.DiscreteLocus;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;
import static org.easymock.EasyMock.*;

/**
 * User: Oleksiy Pylypenko
 * At: 2/8/13  1:22 PM
 */
public class DiscreteLocusTest {
    private DiscreteLocus locus;

    @Before
    public void setUp() throws Exception {
        locus = new DiscreteLocus(new int[][]{
                new int[] {0, 3, 7},
                new int[] {1, 2, 5}
        });
    }

    @Test
    public void testGetSize() throws Exception {
        assertEquals(new RectRange(0, 0, 8, 2), locus.getSize());
    }

    @Test
    public void testDraw() throws Exception {
        PixelDrawer drawer = createMock(PixelDrawer.class);

        drawer.put(0, 0);
        drawer.put(3, 0);
        drawer.put(7, 0);
        drawer.put(1, 1);
        drawer.put(2, 1);
        drawer.put(5, 1);

        replay(drawer);

        locus.draw(new RectRange(0, 0, 8, 2), drawer);

        verify(drawer);
    }
}
