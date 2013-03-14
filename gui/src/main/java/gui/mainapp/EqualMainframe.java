package gui.mainapp;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/5/13
 * Time: 7:31 AM
 */
public class EqualMainframe {
    private JTextArea equationPad;
    private JPanel root;
    private JPanel sidePanel;
    private JButton refreshButton;
    private JSlider timeSlider;
    private JButton playButton;
    private JButton upButton;
    private JButton leftButton;
    private JButton downButton;
    private JButton rightButton;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JLabel constantsLabel;
    private JLabel variablesLabel;

    public EqualMainframe(final ViewModel viewModel) {
        setData(viewModel);

        bindAction(viewModel, refreshButton, KeyStroke.getKeyStroke("F2"), ViewModel.ActionType.REFRESH);
        bindAction(viewModel, playButton,    KeyStroke.getKeyStroke("F2"), ViewModel.ActionType.PLAY);
        bindAction(viewModel, upButton,      KeyStroke.getKeyStroke("F2"), ViewModel.ActionType.UP);
        bindAction(viewModel, downButton,    KeyStroke.getKeyStroke("F2"), ViewModel.ActionType.DOWN);
        bindAction(viewModel, leftButton,    KeyStroke.getKeyStroke("F2"), ViewModel.ActionType.LEFT);
        bindAction(viewModel, rightButton,   KeyStroke.getKeyStroke("F2"), ViewModel.ActionType.RIGHT);
        bindAction(viewModel, zoomInButton,  KeyStroke.getKeyStroke("F2"), ViewModel.ActionType.ZOOM_IN);
        bindAction(viewModel, zoomOutButton, KeyStroke.getKeyStroke("F2"), ViewModel.ActionType.ZOOM_OUT);

        equationPad
                .getDocument()
                .addDocumentListener(
                        new EquationUpdater(viewModel));

        mapAllActions(viewModel);
    }

    private void mapAllActions(final ViewModel viewModel) {
        for (final ViewModel.ActionType actionType : ViewModel.ActionType.values()) {
        }
    }

    private void bindAction(final ViewModel viewModel,
                            JButton button,
                            KeyStroke key,
                            final ViewModel.ActionType actionType) {
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewModel.action(actionType);
            }
        };

        root.getActionMap().put(actionType.toString(), action);
        button.setAction(action);

        root.getInputMap()
                .put(key, actionType.toString());
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("equal5");
        ViewModel model = new ViewModel();
        frame.setContentPane(new EqualMainframe(model).root);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1000, 600);
        frame.setVisible(true);
    }

    private void setData(ViewModel data) {
        equationPad.setText(data.getEquations());
    }

    private static class ViewModelActionReaction implements ActionListener {
        private final ViewModel viewModel;
        private ViewModel.ActionType actionType;

        public ViewModelActionReaction(ViewModel viewModel, ViewModel.ActionType actionType) {
            this.viewModel = viewModel;
            this.actionType = actionType;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            viewModel.action(actionType);
        }
    }

    private class EquationUpdater implements DocumentListener {
        private final ViewModel viewModel;

        public EquationUpdater(ViewModel viewModel) {
            this.viewModel = viewModel;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateModel();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateModel();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateModel();
        }

        private void updateModel() {
            viewModel.setEquations(equationPad.getText());
        }
    }
}
