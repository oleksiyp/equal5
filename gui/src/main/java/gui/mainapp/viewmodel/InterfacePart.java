package gui.mainapp.viewmodel;

/**
* User: Oleksiy Pylypenko
* Date: 3/23/13
* Time: 1:32 PM
*/
public enum InterfacePart {
    CONSTANTS, VARIABLES, EQUATION, VIEWPORT, TIME_CONTROL;

    public void accept(InterfacePartVisitor visitor) {
        switch (this) {
            case CONSTANTS: visitor.constants(); break;
            case VARIABLES: visitor.variables(); break;
            case EQUATION: visitor.equation(); break;
            case VIEWPORT: visitor.viewport(); break;
            case TIME_CONTROL: visitor.timeControl(); break;
        }
    }
}
