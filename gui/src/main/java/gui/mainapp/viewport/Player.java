package gui.mainapp.viewport;

import gui.mainapp.EqualAppPanel;
import gui.mainapp.viewmodel.EqualViewModel;
import gui.mainapp.viewmodel.PlayStateVisitor;

import java.util.ArrayList;
import java.util.List;

/**
* User: Oleksiy Pylypenko
* At: 4/1/13  2:49 PM
*/
public class Player implements PlayStateVisitor, FrameListener {
    private final List<PlayerListener> listeners = new ArrayList<PlayerListener>();
    private EqualAppPanel equalAppPanel;
    private final EqualViewport viewport;
    private final EqualViewModel viewmodel;

    public Player(EqualAppPanel equalAppPanel) {
        this.equalAppPanel = equalAppPanel;
        viewport = equalAppPanel.getEqualViewport();
        viewmodel = equalAppPanel.getEqualViewmodel();
    }

    @Override
    public void play() {
        viewmodel.getPlayStateControl().play();

        viewport.addFrameListener(this);
        viewport.setRecalculateEachSubmit(true);
        viewport.setDelayedRecalculation(false);
        viewmodel.setT(0);
        for (PlayerListener listener : listeners) {
            listener.onPlay();
        }
    }

    @Override
    public void stop() {
        viewmodel.getPlayStateControl().stop();

        EqualViewport viewport = equalAppPanel.getEqualViewport();

        viewport.setRecalculateEachSubmit(false);
        viewport.setDelayedRecalculation(true);
        viewport.removeFrameListener(this);
        for (PlayerListener listener : listeners) {
            listener.onStop();
        }
    }

    @Override
    public void frameDone() {
        int t = viewmodel.getT();
        boolean stopping = false;
        if (t >= viewmodel.getSteps()) {
            stopping = true;
        } else {
            viewmodel.setT(t + 1);
        }
        for (PlayerListener listener : listeners) {
            listener.onFrameChanged();
        }
        if (stopping) {
            viewmodel.getPlayStateControl().stop();
        }
    }

    public void addPlayerListener(PlayerListener listener) {
        listeners.add(listener);
    }

    public void removePlayerListener(PlayerListener listener) {
        listeners.add(listener);
    }
}
