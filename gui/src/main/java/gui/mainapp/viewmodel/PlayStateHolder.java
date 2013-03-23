package gui.mainapp.viewmodel;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/23/13
 * Time: 1:45 PM
 */
public class PlayStateHolder implements PlayStateControl {

    private PlayState state = PlayState.STOP;

    private final EqualViewModel viewModel;
    public PlayStateHolder(EqualViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void togglePlaying() {
        changeToState(state.opposite());
    }

    public void play() {
        changeToState(PlayState.PLAY);
    }

    public void stop() {
        changeToState(PlayState.STOP);
    }

    private void changeToState(PlayState newState) {
        boolean changed = false;
        synchronized (this) {
            if (state != newState) {
                state = newState;
                changed = true;
            }
        }
        if (changed) {
            viewModel
                .broadcastViewListener
                .onPlayStateChange(state);
        }
    }
}
