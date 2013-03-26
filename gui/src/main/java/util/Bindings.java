package util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/26/13
 * Time: 6:30 PM
 */
public class Bindings {
    public static void bind(BeanControl toCtl, String toProp,
                     BeanControl fromCtl, String fromProp) {
        toCtl.addPropertyChangeListener(
                new SyncListener(toProp, fromCtl, fromProp)
        );
        fromCtl.addPropertyChangeListener(
                new SyncListener(fromProp, toCtl, toProp)
        );
        toCtl.set(toProp, fromCtl.get(fromProp));
    }

    private static class SyncListener implements PropertyChangeListener {
        private final String property;
        private final BeanControl control2;
        private final String property2;

        public SyncListener(String property,
                            BeanControl control2, String property2) {
            this.property = property;
            this.control2 = control2;
            this.property2 = property2;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (!property.equals(evt.getPropertyName())) {
                return;
            }
            control2.set(property2, evt.getNewValue());
        }
    }
}
