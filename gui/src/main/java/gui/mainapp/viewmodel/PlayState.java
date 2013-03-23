package gui.mainapp.viewmodel;

/**
* User: Oleksiy Pylypenko
* Date: 3/23/13
* Time: 1:51 PM
*/
public enum PlayState {
    PLAY, STOP;

    public PlayState opposite() {
        switch (this) {
            case PLAY: return STOP;
            case STOP: return PLAY;
        }
        throw new UnsupportedOperationException("opposite");
    }

    public void accept(PlayStateVisitor visitor) {
        switch (this) {
            case PLAY: visitor.play(); return;
            case STOP: visitor.stop(); return;
        }
        throw new UnsupportedOperationException("opposite");
    }
}
