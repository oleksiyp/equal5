package gui.mainapp.viewport;

import engine.calculation.CalculationParameters;
import engine.calculation.ViewportBounds;
import engine.calculation.ViewportSize;

import java.awt.*;
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
            protected int c;

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
                project();
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
                if (!inViewport()) {
                    return;
                }
                drawLine();
                if (type == LineType.ZERO) {
                    for (Line line : perpendicularLines) {
                        line.drawTickAlongLine(
                                c - options.getTickSize(),
                                c + options.getTickSize());
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
                return c >= -20 && c < size.getWidth() + 20;
            }

            @Override
            public void drawLine() {
                g.drawLine(c, 0, c, size.getHeight());
            }

            @Override
            public void drawTickAlongLine(int c1, int c2) {
                g.drawLine(c, c1, c, c2);
            }
        }

        private class HorizontalLine extends Line {
            private HorizontalLine(double value, LineType type) {
                super(value, type);
            }

            @Override
            protected void drawTickAlongLine(int c1, int c2) {
                g.drawLine(c1, c, c2, c);
            }

            @Override
            public void project() {
                c = (int) Math.round(bounds.projectY(value, size));
            }

            @Override
            public boolean inViewport() {
                return c >= -20 && c < size.getHeight() + 20;
            }

            @Override
            public void drawLine() {
                g.drawLine(0, c, size.getWidth(), c);
            }
        }

    }

    public class Options {
        private Color zeroColor = Color.BLACK;
        private Color mainColor = Color.GRAY;
        private Color secondaryColor = Color.LIGHT_GRAY;
        private int tickSize = 4;

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
            this.zeroColor = zeroColor;
        }

        public void setMainColor(Color mainColor) {
            this.mainColor = mainColor;
        }

        public void setSecondaryColor(Color secondaryColor) {
            this.secondaryColor = secondaryColor;
        }

        public int getTickSize() {
            return tickSize;
        }
    }

}
