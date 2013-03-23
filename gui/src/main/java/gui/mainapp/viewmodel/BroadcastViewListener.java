package gui.mainapp.viewmodel;

import java.util.Set;

/**
* User: Oleksiy Pylypenko
* Date: 3/23/13
* Time: 1:48 PM
*/
class BroadcastViewListener implements ViewListener {
    private EqualViewModel viewModel;

    public BroadcastViewListener(EqualViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void onUpdate(Set<InterfacePart> parts) {
        for (ViewListener listener : viewModel.viewListeners) {
            listener.onUpdate(parts);
        }
    }

    @Override
    public void onPlayStateChange(PlayState state) {
        for (ViewListener listener : viewModel.viewListeners) {
            listener.onPlayStateChange(state);
        }
    }
}
