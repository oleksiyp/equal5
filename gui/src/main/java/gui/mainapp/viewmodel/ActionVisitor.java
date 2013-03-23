package gui.mainapp.viewmodel;

/**
* User: Oleksiy Pylypenko
* Date: 3/23/13
* Time: 1:32 PM
*/
public interface ActionVisitor {
    void refresh();

    void play();

    void left();

    void right();

    void up();

    void down();

    void zoomIn();

    void zoomOut();

    void lowerT();

    void raiseT();
}
