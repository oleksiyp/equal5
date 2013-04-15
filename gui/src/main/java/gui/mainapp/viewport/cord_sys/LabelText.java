package gui.mainapp.viewport.cord_sys;

/**
* User: Oleksiy Pylypenko
* At: 4/15/13  6:08 PM
*/
abstract class LabelText {
    public abstract String getText(int size);

    public static LabelText number(double number) {
        return new NumberText(number);
    }
}

class NumberText extends LabelText {
    private double value;

    NumberText(double value) {
        this.value = value;
    }

    @Override
    public String getText(int size) {
        return Double.toString(size);
    }
}
