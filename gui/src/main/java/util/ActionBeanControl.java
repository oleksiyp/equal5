package util;

import javax.swing.*;
import java.beans.PropertyChangeListener;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/26/13
 * Time: 6:24 PM
 */
public class ActionBeanControl implements BeanControl {
    private final Action action;

    public ActionBeanControl(Action action) {
        this.action = action;
    }

    @Override
    public String[] getPropertyNames() {
        return new String[] {
                Action.DEFAULT,
                Action.NAME,
                Action.SHORT_DESCRIPTION,
                Action.LONG_DESCRIPTION,
                Action.SMALL_ICON,
                Action.ACTION_COMMAND_KEY,
                Action.ACCELERATOR_KEY,
                Action.MNEMONIC_KEY,
                Action.SELECTED_KEY,
                Action.DISPLAYED_MNEMONIC_INDEX_KEY,
                Action.LARGE_ICON_KEY
        };
    }

    @Override
    public void set(String name, Object value) {
        action.putValue(name, value);
    }

    @Override
    public Object get(String name) {
        return action.getValue(name);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        action.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        action.removePropertyChangeListener(listener);
    }
}
