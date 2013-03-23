package gui.mainapp.viewmodel;

/**
* User: Oleksiy Pylypenko
* Date: 3/23/13
* Time: 1:31 PM
*/
public enum ActionType {
    REFRESH,
    PLAY,

    LEFT,
    RIGHT,
    UP,
    DOWN,

    ZOOM_IN,
    ZOOM_OUT,

    RAISE_T,
    LOWER_T;

    public void accept(ActionVisitor visitor) {
        switch (this) {
            case REFRESH: visitor.refresh(); return;
            case PLAY: visitor.play(); return;
            case LEFT: visitor.left(); return;
            case RIGHT: visitor.right(); return;
            case UP: visitor.up(); return;
            case DOWN: visitor.down(); return;
            case ZOOM_IN: visitor.zoomIn(); return;
            case ZOOM_OUT: visitor.zoomOut(); return;
            case LOWER_T: visitor.lowerT(); return;
            case RAISE_T: visitor.raiseT(); return;
        }
        throw new UnsupportedOperationException("accept(" + this + ")");
    }
}
