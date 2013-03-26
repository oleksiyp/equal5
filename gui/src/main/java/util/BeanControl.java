package util;

import java.beans.PropertyChangeListener;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/26/13
 * Time: 5:07 PM
 */
public interface BeanControl {
    String []getPropertyNames();

    void set(String name, Object value);

    Object get(String name);

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);
}
