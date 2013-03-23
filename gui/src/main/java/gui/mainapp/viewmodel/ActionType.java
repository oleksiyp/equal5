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
            case REFRESH: visitor.refresh(); break;
            case PLAY: visitor.play(); break;
            case LEFT: visitor.left(); break;
            case RIGHT: visitor.right(); break;
            case UP: visitor.up(); break;
            case DOWN: visitor.down(); break;
            case ZOOM_IN: visitor.zoomIn(); break;
            case ZOOM_OUT: visitor.zoomOut(); break;
            case LOWER_T: visitor.lowerT(); break;
            case RAISE_T: visitor.raiseT(); break;
        }
    }
}
