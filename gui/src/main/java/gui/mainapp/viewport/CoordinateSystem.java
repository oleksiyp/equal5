package gui.mainapp.viewport;

import engine.calculation.CalculationParameters;
import engine.calculation.ViewportBounds;
import engine.calculation.ViewportSize;
import util.BeanControl;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/26/13
 * Time: 12:08 AM
 */
public class CoordinateSystem {
    public static final double EPS = 1e-8;

    private final Options options = new Options();

    public Options getOptions() {
        return options;
    }

    public void draw(Graphics g, CalculationParameters params) {
        if (!options.isVisible()) {
            return;
        }
        new PaintInstance(g, params).draw();
    }

    enum LineType {
        SECONDARY,
        MAIN,
        ZERO
    }

    class PaintInstance {
        private final Graphics g;
        private final CalculationParameters params;
        private final ViewportBounds bounds;
        private final ViewportSize size;

        public PaintInstance(Graphics g, CalculationParameters params) {
            this.g = g;
            this.params = params;
            bounds = params.getBounds();
            size = params.getSize();
        }

        private void draw() {

            List<Line> xLines = createSegmentation(true,
                    bounds.getLeft(),
                    bounds.getRight());
            List<Line> yLines = createSegmentation(false,
                    bounds.getTop(),
                    bounds.getBottom());

            for (LineType t : Arrays.asList(
                    LineType.SECONDARY,
                    LineType.MAIN,
                    LineType.ZERO)) { // deal with overlaps
                if (!options.isShowGrid()) {
                    if (t == LineType.SECONDARY
                            || t == LineType.MAIN) {
                        continue;
                    }
                }
                drawByType(xLines, yLines, t);
                drawByType(yLines, xLines, t);
            }
        }

        private void drawByType(List<Line> lines,
                                List<Line> perpendicularLines,
                                LineType filterType) {
            for (Line line : lines) {
                if (filterType != line.getType()) {
                    continue;
                }

                line.draw(perpendicularLines);
            }
        }

        private List<Line> createSegmentation(boolean vertical, double from, double to) {
            double min = Math.min(from, to);
            double max = Math.max(from, to);

            double degree = Math.floor(Math.log10(Math.abs(max - min)));

            double delta = Math.pow(10, degree - 1);
            double deltaMainDiv = Math.pow(10, degree);
            double start = min - Math.IEEEremainder(min, delta);
            double end = max + delta;

            List<Line> lines = new ArrayList<Line>();
            for (double val = start; val < end; val += delta) {
                LineType type = LineType.SECONDARY;
                if (Math.abs(val) < EPS) {
                    type = LineType.ZERO;
                } else if (Math.abs(Math.IEEEremainder(val, deltaMainDiv)) < EPS) {
                    type = LineType.MAIN;
                }

                lines.add(createLine(vertical, val, type));
            }
            return lines;
        }

        public Line createLine(boolean vertical, double val, LineType type) {
            return vertical ?
                    new VerticalLine(val, type) :
                    new HorizontalLine(val, type);
        }

        private abstract class Line {

            protected final double value;
            protected final LineType type;
            protected Integer c = null;

            protected int coord() {
                if (c == null) {
                    project();
                }
                return c;
            }
            private Line(double value, LineType type) {
                this.value = value;
                this.type = type;
            }

            public LineType getType() {
                return type;
            }

            @Override
            public String toString() {
                return type + " " + value;
            }

            public void draw(List<Line> perpendicularLines) {
                if (!inViewport()) {
                    return;
                }
                switch (type) {
                    case ZERO:
                        g.setColor(options.getZeroColor());
                        break;
                    case MAIN:
                        g.setColor(options.getMainColor());
                        break;
                    case SECONDARY:
                        g.setColor(options.getSecondaryColor());
                        break;
                }
                drawLine();
                if (type == LineType.ZERO) {
                    for (Line line : perpendicularLines) {
                        line.drawTickAlongLine(
                                coord() - options.getTickSize(),
                                coord() + options.getTickSize());
                    }
                }
            }

            protected abstract void drawTickAlongLine(int c1, int c2);

            public abstract void project();

            public abstract boolean inViewport();

            public abstract void drawLine();
        }

        private class VerticalLine extends Line {
            private VerticalLine(double value, LineType type) {
                super(value, type);
            }

            @Override
            public void project() {
                c = (int) Math.round(bounds.projectX(value, size));
            }

            @Override
            public boolean inViewport() {
                return coord() >= -20 && coord() < size.getWidth() + 20;
            }

            @Override
            public void drawLine() {
                g.drawLine(coord(), 0, coord(), size.getHeight());
            }

            @Override
            public void drawTickAlongLine(int c1, int c2) {
                g.drawLine(coord(), c1, coord(), c2);
            }
        }

        private class HorizontalLine extends Line {
            private HorizontalLine(double value, LineType type) {
                super(value, type);
            }

            @Override
            protected void drawTickAlongLine(int c1, int c2) {
                g.drawLine(c1, coord(), c2, coord());
            }

            @Override
            public void project() {
                c = (int) Math.round(bounds.projectY(value, size));
            }

            @Override
            public boolean inViewport() {
                return coord() >= -20 && coord() < size.getHeight() + 20;
            }

            @Override
            public void drawLine() {
                g.drawLine(0, coord(), size.getWidth(), coord());
            }
        }

    }

    public interface OptionProperties {
        String ZERO_COLOR_PROPERTY = "zeroColor";
        String MAIN_COLOR_PROPERTY = "mainColor";
        String SECONDARY_COLOR_PROPERTY = "secondaryColor";
        String TICK_SIZE_PROPERTY = "tickSize";
        String VISIBLE_PROPERTY = "visible";
        String SHOW_GRID_PROPERTY = "showGrid";

        String[] ALL = new String[]{
                ZERO_COLOR_PROPERTY,
                MAIN_COLOR_PROPERTY,
                SECONDARY_COLOR_PROPERTY,
                TICK_SIZE_PROPERTY,
                VISIBLE_PROPERTY,
                SHOW_GRID_PROPERTY
        };
    }

    public class Options implements BeanControl {
        private PropertyChangeSupport changes = new PropertyChangeSupport(this);

        private Color zeroColor = Color.BLACK;
        private Color mainColor = Color.GRAY;
        private Color secondaryColor = Color.LIGHT_GRAY;
        private int tickSize = 4;
        private boolean visible = true;
        private boolean showGrid;

        public Color getZeroColor() {
            return zeroColor;
        }

        public Color getMainColor() {
            return mainColor;
        }

        public Color getSecondaryColor() {
            return secondaryColor;
        }

        public void setZeroColor(Color zeroColor) {
            if (zeroColor == null) {
                throw new IllegalArgumentException(OptionProperties.ZERO_COLOR_PROPERTY);
            }
            Color oldZeroColor = this.zeroColor;
            if (!zeroColor.equals(this.zeroColor)) {
                this.zeroColor = zeroColor;
                changes.firePropertyChange(OptionProperties.ZERO_COLOR_PROPERTY, oldZeroColor, zeroColor);
            }
        }

        public void setMainColor(Color mainColor) {
            if (mainColor == null) {
                throw new IllegalArgumentException(OptionProperties.MAIN_COLOR_PROPERTY);
            }
            Color oldMainColor = this.mainColor;
            if (!mainColor.equals(this.mainColor)) {
                this.mainColor = mainColor;
                changes.firePropertyChange(OptionProperties.MAIN_COLOR_PROPERTY, oldMainColor, mainColor);
            }
        }

        public void setSecondaryColor(Color secondaryColor) {
            if (secondaryColor == null) {
                throw new IllegalArgumentException(OptionProperties.SECONDARY_COLOR_PROPERTY);
            }
            Color oldSecondaryColor = this.secondaryColor;
            if (!secondaryColor.equals(this.secondaryColor)) {
                this.secondaryColor = secondaryColor;
                changes.firePropertyChange(OptionProperties.SECONDARY_COLOR_PROPERTY, oldSecondaryColor, secondaryColor);
            }
        }

        public int getTickSize() {
            return tickSize;
        }

        public void setTickSize(int tickSize) {
            if (tickSize < 0) {
                throw new IllegalArgumentException(OptionProperties.TICK_SIZE_PROPERTY);
            }
            int oldTickSize = this.tickSize;
            if (this.tickSize != tickSize) {
                this.tickSize = tickSize;
                changes.firePropertyChange(OptionProperties.TICK_SIZE_PROPERTY, oldTickSize, tickSize);
            }
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            boolean oldVisible = this.visible;
            if (this.visible != visible) {
                this.visible = visible;
                changes.firePropertyChange(OptionProperties.VISIBLE_PROPERTY, oldVisible, visible);
            }
        }

        public boolean isShowGrid() {
            return showGrid;
        }

        public void setShowGrid(boolean showGrid) {
            boolean oldShowGrid = this.showGrid;
            if (this.showGrid != showGrid) {
                this.showGrid = showGrid;
                changes.firePropertyChange(OptionProperties.SHOW_GRID_PROPERTY, oldShowGrid, showGrid);
            }
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            changes.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            changes.removePropertyChangeListener(listener);
        }

        @Override
        public String[] getPropertyNames() {
            return OptionProperties.ALL;
        }

        @Override
        public void set(String name, Object value) {
            if (OptionProperties.ZERO_COLOR_PROPERTY.equals(name)) {
                setZeroColor((Color) value);
                return;
            } else if (OptionProperties.MAIN_COLOR_PROPERTY.equals(name)) {
                setMainColor((Color) value);
                return;
            } else if (OptionProperties.SECONDARY_COLOR_PROPERTY.equals(name)) {
                setSecondaryColor((Color) value);
                return;
            } else if (OptionProperties.TICK_SIZE_PROPERTY.equals(name)) {
                setTickSize((Integer) value);
                return;
            } else if (OptionProperties.VISIBLE_PROPERTY.equals(name)) {
                setVisible((Boolean) value);
                return;
            } else if (OptionProperties.SHOW_GRID_PROPERTY.equals(name)) {
                setShowGrid((Boolean) value);
                return;
            }
            throw new UnsupportedOperationException("set('" + name + "', '" + value + "')");
        }

        @Override
        public Object get(String name) {
            if (OptionProperties.ZERO_COLOR_PROPERTY.equals(name)) {
                return getZeroColor();
            } else if (OptionProperties.MAIN_COLOR_PROPERTY.equals(name)) {
                return getMainColor();
            } else if (OptionProperties.SECONDARY_COLOR_PROPERTY.equals(name)) {
                return getSecondaryColor();
            } else if (OptionProperties.TICK_SIZE_PROPERTY.equals(name)) {
                return getTickSize();
            } else if (OptionProperties.VISIBLE_PROPERTY.equals(name)) {
                return isVisible();
            } else if (OptionProperties.SHOW_GRID_PROPERTY.equals(name)) {
                return isShowGrid();
            }
            throw new UnsupportedOperationException("get('" + name + "')");
        }
    }

}
