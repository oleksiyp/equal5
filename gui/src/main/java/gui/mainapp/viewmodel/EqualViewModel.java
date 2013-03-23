package gui.mainapp.viewmodel;

import engine.calculation.ViewportBounds;

import java.awt.*;
import java.util.*;
import java.util.List;

public class EqualViewModel {
    public static final int MOVE_PART = 10;
    public static final double ZOOM_COEFFICIENT = 1.4;

    final List<ViewListener> viewListeners = new ArrayList<ViewListener>();

    final ActionHandler actionHandler;
    final PlayStateHolder playStateHolder;
    final BroadcastViewListener broadcastViewListener = new BroadcastViewListener(this);

    private String equations;
    private int t;
    private int steps;
    private Dimension viewportSize;
    private ViewportBounds viewportBounds;

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
}