package gui.mainapp;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * User: Oleksiy Pylypenko
 * At: 3/20/13  1:30 PM
 */
public class ViewModelAction extends AbstractAction {
    private ViewModel viewModel;
    private ViewModel.ActionType actionType;

    public ViewModelAction(ViewModel viewModel, ViewModel.ActionType actionType) {
        this.viewModel = viewModel;
        this.actionType = actionType;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        viewModel.action(actionType);
    }

    public ViewModelAction fillTextAndIcon(JButton fromButton) {
        putValue(Action.NAME, fromButton.getText());
        putValue(Action.SMALL_ICON, fromButton.getIcon());
        return this;
    }

    public ViewModelAction putActionMap(JComponent component) {
        component.getActionMap().put(actionType.toString(), this);
        return this;
    }

    public ViewModelAction bindKey(JComponent component, KeyStroke key) {
        component.getInputMap()
                .put(key, actionType.toString());
        return this;
    }

    public ViewModelAction bind(AbstractButton button) {
        button.setAction(this);
        return this;
    }
}
