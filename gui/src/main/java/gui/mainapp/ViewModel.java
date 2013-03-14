package gui.mainapp;

public class ViewModel {
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

    private String equations;

    public ViewModel() {
    }

    public String getEquations() {
        return equations;
    }

    public void setEquations(final String equations) {
        this.equations = equations;
    }

    public void action(ActionType type) {

    }
}