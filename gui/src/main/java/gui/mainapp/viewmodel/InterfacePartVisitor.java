package gui.mainapp.viewmodel;

/**
* User: Oleksiy Pylypenko
* Date: 3/23/13
* Time: 1:32 PM
*/
public interface InterfacePartVisitor {
    void constants();

    void variables();

    void equation();

    void viewport();

    void timeControl();
}
