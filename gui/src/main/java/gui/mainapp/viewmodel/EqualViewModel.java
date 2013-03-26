package gui.mainapp.viewmodel;

import engine.calculation.ViewportBounds;
import engine.calculation.ViewportSize;
import util.BeanControl;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.List;

public class EqualViewModel implements BeanControl {
    public static final int MOVE_PART = 10;
    public static final double ZOOM_COEFFICIENT = 1.4;

    final List<ViewListener> viewListeners = new ArrayList<ViewListener>();

    final ActionHandler actionHandler;
    final PlayStateHolder playStateHolder;
    final BroadcastViewListener broadcastViewListener = new BroadcastViewListener(this);

    private String equations;
    private int t;
    private int steps;
    private ViewportSize viewportSize;
    private ViewportBounds viewportBounds;
    private boolean keepAspect = true;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    public EqualViewModel() {
        actionHandler = new ActionHandler(this);
        playStateHolder = new PlayStateHolder(this);
        resetToDefaults();
    }

    public void resetToDefaults() {
        equations = "y=";
        t = 0;
        steps = 30;
        viewportBounds = new ViewportBounds(-10, 10, -10, 10);
        viewportSize = new ViewportSize(600, 600);
        keepAspect = true;

        notifyAllViewListeners();
    }

    public boolean isKeepAspect() {
        return keepAspect;
    }

    public void setKeepAspect(boolean keepAspect) {
        if (this.keepAspect != keepAspect) {
            this.keepAspect = keepAspect;
            support.firePropertyChange("keepAspect", !keepAspect, keepAspect);
            maintainAspect();
            notifyViewListeners(InterfacePart.CONSTANTS,
                    InterfacePart.VIEWPORT);
        }
    }

    public String getEquations() {
        return equations;
    }

    public void setEquations(final String equations) {
        this.equations = equations;
        notifyViewListeners(InterfacePart.EQUATION,
                InterfacePart.VIEWPORT);
    }

    public ViewportSize getViewportSize() {
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
        maintainAspect();
        notifyViewListeners(InterfacePart.CONSTANTS,
                InterfacePart.VIEWPORT);
    }

    private void maintainAspect() {
        if (!keepAspect) {
            return;
        }
        if (viewportSize.getWidth() == 0 || viewportSize.getHeight() == 0) {
            return;
        }

        double aspect = viewportSize.getHeight();
        aspect /= viewportSize.getWidth();

        double height = viewportBounds.getWidth() * aspect;

        double y = viewportBounds.getCenterY();

        double top = y + height / 2;
        double bottom = y - height / 2;

        viewportBounds = new ViewportBounds(
                viewportBounds.getLeft(),
                top,
                bottom,
                viewportBounds.getRight());
    }

    public ViewportBounds getViewportBounds() {
        return viewportBounds;
    }

    public void setViewportSize(ViewportSize viewportSize) {
        this.viewportSize = viewportSize;
        maintainAspect();
        notifyViewListeners(InterfacePart.CONSTANTS,
                InterfacePart.VIEWPORT);
    }

    public String getConstantsStatus() {
        return String.format("\u21D0 %.4g \u21D1 %.4g \u21D3 %.4g \u21D2 %.4g \u21F2 %dx%d \u21DD %d",
                viewportBounds.getLeft(), viewportBounds.getTop(),
                viewportBounds.getBottom(), viewportBounds.getRight(),
                viewportSize.getWidth(), viewportSize.getHeight(),
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

    void notifyViewListeners(InterfacePart firstPart, InterfacePart... rest) {
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
        type.accept(actionHandler);
    }

    public PlayStateControl getPlayStateControl() {
        return playStateHolder;
    }

    @Override
    public String[] getPropertyNames() {
        return new String[] {
                "keepAspect"
        };
    }

    @Override
    public void set(String name, Object value) {
        if ("keepAspect".equals(name)) {
            setKeepAspect((Boolean) value);
            return;
        }
        throw new UnsupportedOperationException("set('" + name + "', '"
                + value + "')");
    }

    @Override
    public Object get(String name) {
        if ("keepAspect".equals(name)) {
            return isKeepAspect();
        }
        throw new UnsupportedOperationException("get('" + name + "')");
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}