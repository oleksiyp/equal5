package gui.mainapp;

import engine.calculation.ViewportBounds;

import java.awt.*;
import java.util.*;
import java.util.List;

public class EqualViewModel {
    public static final int MOVE_PART = 10;
    private static final double ZOOM_COEFFICIENT = 1.4;

    private EqualViewModel.ActionHandler handler;

    public enum ActionType {
        REFRESH,
        PLAY,

        LEFT,
        RIGHT,
        UP,
        DOWN,

        ZOOM_IN,
        ZOOM_OUT;

        public void accept(ActionVisitor visitor) {
            switch (this) {
                case REFRESH: visitor.refresh(); break;
                case PLAY: visitor.play(); break;
                case LEFT: visitor.left(); break;
                case RIGHT: visitor.right(); break;
                case UP: visitor.up(); break;
                case DOWN: visitor.down(); break;
                case ZOOM_IN: visitor.zoomIn(); break;
                case ZOOM_OUT: visitor.zoomOut(); break;
            }
        }
    }

    public interface ActionVisitor {
        void refresh();

        void play();

        void left();

        void right();

        void up();

        void down();

        void zoomIn();

        void zoomOut();
    }


    public enum InterfacePart {
        CONSTANTS, VARIABLES, EQUATION, VIEWPORT, TIME_CONTROL;

        public void accept(InterfacePartVisitor visitor) {
            switch (this) {
                case CONSTANTS: visitor.constants(); break;
                case VARIABLES: visitor.variables(); break;
                case EQUATION: visitor.equation(); break;
                case VIEWPORT: visitor.viewport(); break;
                case TIME_CONTROL: visitor.timeControl(); break;
            }
        }
    }

    public interface InterfacePartVisitor {
        void constants();

        void variables();

        void equation();

        void viewport();

        void timeControl();
    }

    public interface ViewListener {
        void onUpdate(Set<InterfacePart> parts);
    }

    private String equations;
    private int t;
    private int steps;
    private Dimension viewportSize;
    private ViewportBounds viewportBounds;

    private List<ViewListener> viewListeners = new ArrayList<ViewListener>();

    public EqualViewModel() {
        handler = new ActionHandler();
        resetToDefaults();
    }

    public void resetToDefaults() {
        equations = "y=";
        t = 0;
        steps = 100;
        viewportBounds = new ViewportBounds(-10, 10, -10, 10);
        viewportSize = new Dimension(600, 600);

        notifyAllViewListeners();
    }

    public String getEquations() {
        return equations;
    }

    public void setEquations(final String equations) {
        this.equations = equations;
        notifyViewListeners(InterfacePart.EQUATION,
                InterfacePart.VIEWPORT);
    }

    public Dimension getViewportSize() {
        return viewportSize;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
        notifyViewListeners(
                InterfacePart.TIME_CONTROL,
                InterfacePart.VARIABLES,
                InterfacePart.VIEWPORT);
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
        notifyViewListeners(InterfacePart.TIME_CONTROL);
    }


    public void setViewportBounds(ViewportBounds viewportBounds) {
        this.viewportBounds = viewportBounds;
        notifyViewListeners(InterfacePart.CONSTANTS,
                InterfacePart.VIEWPORT);
    }

    public ViewportBounds getViewportBounds() {
        return viewportBounds;
    }

    public void setViewportSize(Dimension viewportSize) {
        this.viewportSize = viewportSize;
        notifyViewListeners(InterfacePart.CONSTANTS);
    }

    public String getConstantsStatus() {
        return String.format("\u21D0 %.4g \u21D1 %.4g \u21D3 %.4g \u21D2 %.4g \u21F2 %dx%d \u21DD %d",
                viewportBounds.getLeft(), viewportBounds.getTop(),
                viewportBounds.getBottom(), viewportBounds.getRight(),
                viewportSize.width, viewportSize.height,
                steps);
    }


    public String getVariablesStatus() {
        return String.format("t=%.2g", getTAsVariable());
    }

    public double getTAsVariable() {
        return ((double)t) / steps;
    }

    public void addViewListener(ViewListener listener) {
        viewListeners.add(listener);
        listener.onUpdate(EnumSet.allOf(InterfacePart.class));
    }

    public void removeViewListener(ViewListener listener) {
        viewListeners.remove(listener);
    }

    private void notifyViewListeners(InterfacePart firstPart, InterfacePart... rest) {
        Set<InterfacePart> parts = EnumSet.of(firstPart, rest);
        for (ViewListener listener : viewListeners) {
            listener.onUpdate(parts);
        }
    }

    private void notifyAllViewListeners() {
        for (ViewListener listener : viewListeners) {
            listener.onUpdate(EnumSet.allOf(InterfacePart.class));
        }
    }

    public void action(ActionType type) {
        type.accept(handler);
    }

    private class ActionHandler implements ActionVisitor {

        @Override
        public void refresh() {
            notifyViewListeners(InterfacePart.VIEWPORT);
        }

        @Override
        public void play() {
        }

        @Override
        public void left() {
            double dx = viewportBounds.getWidth() / MOVE_PART;
            viewportBounds = viewportBounds.offset(-dx, 0);
            notifyViewListeners(InterfacePart.CONSTANTS, InterfacePart.VIEWPORT);
        }

        @Override
        public void right() {
            double dx = viewportBounds.getWidth() / MOVE_PART;
            viewportBounds = viewportBounds.offset(dx, 0);
            notifyViewListeners(InterfacePart.CONSTANTS, InterfacePart.VIEWPORT);
        }

        @Override
        public void up() {
            double dy = viewportBounds.getHeight() / MOVE_PART;
            viewportBounds = viewportBounds.offset(0, dy);
            notifyViewListeners(InterfacePart.CONSTANTS, InterfacePart.VIEWPORT);
        }

        @Override
        public void down() {
            double dy = viewportBounds.getHeight() / MOVE_PART;
            viewportBounds = viewportBounds.offset(0, -dy);

            notifyViewListeners(InterfacePart.CONSTANTS, InterfacePart.VIEWPORT);
        }

        @Override
        public void zoomIn() {
            viewportBounds = viewportBounds.zoom(1.0 / ZOOM_COEFFICIENT);
            notifyViewListeners(InterfacePart.CONSTANTS, InterfacePart.VIEWPORT);
        }

        @Override
        public void zoomOut() {
            viewportBounds = viewportBounds.zoom(ZOOM_COEFFICIENT);
            notifyViewListeners(InterfacePart.CONSTANTS, InterfacePart.VIEWPORT);
        }
    }
}