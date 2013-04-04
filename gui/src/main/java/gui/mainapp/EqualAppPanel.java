package gui.mainapp;

import engine.calculation.ViewportSize;
import engine.expressions.parser.ExpressionParser;
import engine.expressions.parser.ParsingException;
import engine.expressions.parser.antlr.AntlrExpressionParser;
import gui.mainapp.viewmodel.*;
import gui.mainapp.viewport.CoordinateSystem;
import gui.mainapp.viewport.EqualViewport;
import gui.mainapp.viewport.Player;
import util.ActionBeanControl;
import util.BeanControl;
import util.Bindings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/5/13
 * Time: 7:31 AM
 */
public class EqualAppPanel {
    private JTextArea equationPad;
    private JPanel root;
    private JPanel sidePanel;
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

    private EqualViewport equalViewport;
    private JLabel errorLabel;
    private JButton decrementTButton;
    private JButton incrementTButton;
    private JCheckBox coordinateSystemCheckBox;
    private JCheckBox gridCheckBox;
    private JCheckBox aspectRatioCheckBox;

    private final EqualViewModel viewModel;
    private final Player player;
    private final EqualAppPanelViewListener viewListener;

    private final SyntaxErrorDisplay syntaxErrorDisplay;
    private final AutoCompleter autoCompleter;

    public EqualAppPanel(final EqualViewModel viewModel) {
        this.viewModel = viewModel;

        $$$setupUI$$$();

        // syntax error display
        syntaxErrorDisplay = new SyntaxErrorDisplay(errorLabel, equationPad);

        // parser used for expression parsing
        ExpressionParser parser = configureParser();
        equalViewport.setParser(parser);

        // autocompleter
        autoCompleter = new AutoCompleter(equationPad, parser);

        // player
        player = new Player(this);

        // listener connecting EqualAppPanel <-> EqualViewModel
        viewListener = new EqualAppPanelViewListener();
        viewModel.addViewListener(viewListener);

        // keyboard bindings
        bindButtonAction(viewModel, playButton, KeyStroke.getKeyStroke("F5"), ActionType.PLAY);
        bindButtonAction(viewModel, zoomInButton, KeyStroke.getKeyStroke("F8"), ActionType.ZOOM_IN);
        bindButtonAction(viewModel, zoomOutButton, KeyStroke.getKeyStroke("F7"), ActionType.ZOOM_OUT);
        bindButtonAction(viewModel, decrementTButton, KeyStroke.getKeyStroke("F3"), ActionType.LOWER_T);
        bindButtonAction(viewModel, incrementTButton, KeyStroke.getKeyStroke("F4"), ActionType.RAISE_T);
        bindButtonAction(viewModel, leftButton, KeyStroke.getKeyStroke("F9"), ActionType.LEFT);
        bindButtonAction(viewModel, upButton, KeyStroke.getKeyStroke("F10"), ActionType.UP);
        bindButtonAction(viewModel, downButton, KeyStroke.getKeyStroke("F11"), ActionType.DOWN);
        bindButtonAction(viewModel, rightButton, KeyStroke.getKeyStroke("F12"), ActionType.RIGHT);

        // updater if document changed
        equationPad
                .getDocument()
                .addDocumentListener(
                        new EquationUpdater(viewModel));

        //
        equalViewport.addComponentListener(new ViewportSizeListener());
        equalViewport.setRecalculateEachSubmit(false);
        equalViewport.setDelayedRecalculation(true);

        timeSlider.addChangeListener(new TimeSliderUpdater());

        Action coordSysAction = checkBoxAction(coordinateSystemCheckBox,
                equalViewport
                        .getCoordinateSystem()
                        .getOptions(),
                CoordinateSystem.OptionProperties.VISIBLE_PROPERTY);
        coordinateSystemCheckBox.setAction(coordSysAction);

        Action gridAction = checkBoxAction(gridCheckBox,
                equalViewport
                        .getCoordinateSystem()
                        .getOptions(),
                CoordinateSystem.OptionProperties.SHOW_GRID_PROPERTY);

        Bindings.bind(new ActionBeanControl(gridAction),
                ActionBeanControl.ENABLED,
                equalViewport
                        .getCoordinateSystem()
                        .getOptions(),
                CoordinateSystem.OptionProperties.VISIBLE_PROPERTY);
        gridCheckBox.setAction(gridAction);

        Action aspectAction = checkBoxAction(
                aspectRatioCheckBox,
                viewModel,
                "keepAspect");
        aspectRatioCheckBox.setAction(aspectAction);

        coordinateSystemCheckBox.setMnemonic('c');
        gridCheckBox.setMnemonic('g');
        aspectRatioCheckBox.setMnemonic('a');

        String text = equationPad.getText();
        if (text.matches("y\\s*=")) {
            equationPad.setCaretPosition(text.length());
        }

        equationPad.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (KeyEvent.VK_SPACE == e.getKeyCode()
                        && e.isControlDown()) {
                    e.consume();

                    autoCompleter.run();
                }
            }
        });
    }

    private Action checkBoxAction(JCheckBox checkBox,
                                  BeanControl control, String property) {
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };

        action.putValue(Action.NAME, checkBox.getText());

        Bindings.bind(new ActionBeanControl(action),
                Action.SELECTED_KEY,
                control, property);

        return action;
    }

    private ExpressionParser configureParser() {
        ExpressionParser parser = new AntlrExpressionParser();

        HashMap<String, Double> knownConstants = new HashMap<String, Double>();
        knownConstants.put("pi", Math.PI);
        knownConstants.put("e", Math.E);

        parser.setKnownConstants(knownConstants);
        parser.setVarList(Arrays.asList("x", "y", "t"));
        return parser;
    }

    private void bindButtonAction(EqualViewModel viewModel,
                                  JButton button,
                                  KeyStroke key,
                                  ActionType actionType) {
        new ViewModelAction(viewModel, actionType)
                .fillTextAndIcon(button)
                .putActionMap(root)
                .bindKey(root, key)
                .bind(button);
    }

    public void createUIComponents() {
        equalViewport = new EqualViewport();
    }

    public void beAContentPaneOf(JFrame frame) {
        frame.setContentPane(root);
    }

    public Player getPlayer() {
        return player;
    }

    public EqualViewport getEqualViewport() {
        return equalViewport;
    }

    public EqualViewModel getEqualViewmodel() {
        return viewModel;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        root = new JPanel();
        root.setLayout(new BorderLayout(0, 0));
        final JToolBar toolBar1 = new JToolBar();
        root.add(toolBar1, BorderLayout.SOUTH);
        constantsLabel = new JLabel();
        constantsLabel.setText("LEFT(-10) TOP(10) RIGHT(10) BOTTOM(-10) STEPS(100) WIDTH(800) HEIGHT(600)");
        toolBar1.add(constantsLabel);
        final JToolBar.Separator toolBar$Separator1 = new JToolBar.Separator();
        toolBar1.add(toolBar$Separator1);
        variablesLabel = new JLabel();
        variablesLabel.setText("t(0)");
        toolBar1.add(variablesLabel);
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(350);
        root.add(splitPane1, BorderLayout.CENTER);
        sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout(0, 0));
        splitPane1.setLeftComponent(sidePanel);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        sidePanel.add(panel1, BorderLayout.SOUTH);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel1.add(panel2, BorderLayout.CENTER);
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Controls"));
        rightButton = new JButton();
        rightButton.setHorizontalTextPosition(11);
        rightButton.setIcon(new ImageIcon(getClass().getResource("/gui/mainapp/right-arrow.png")));
        rightButton.setText("F12");
        rightButton.setToolTipText("Move viewport right");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(rightButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 10;
        panel2.add(spacer1, gbc);
        leftButton = new JButton();
        leftButton.setHorizontalTextPosition(10);
        leftButton.setIcon(new ImageIcon(getClass().getResource("/gui/mainapp/left-arrow.png")));
        leftButton.setText("F9");
        leftButton.setToolTipText("Move viewport left");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(leftButton, gbc);
        upButton = new JButton();
        upButton.setHorizontalTextPosition(11);
        upButton.setIcon(new ImageIcon(getClass().getResource("/gui/mainapp/up-arrow.png")));
        upButton.setText("F10");
        upButton.setToolTipText("Move viewort up");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(upButton, gbc);
        downButton = new JButton();
        downButton.setHorizontalTextPosition(10);
        downButton.setIcon(new ImageIcon(getClass().getResource("/gui/mainapp/down-arrow.png")));
        downButton.setText("F11");
        downButton.setToolTipText("Move viewport down");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(downButton, gbc);
        decrementTButton = new JButton();
        decrementTButton.setHorizontalTextPosition(10);
        decrementTButton.setIcon(new ImageIcon(getClass().getResource("/gui/mainapp/previous.png")));
        decrementTButton.setText("F3");
        decrementTButton.setToolTipText("Decrement parameter \"t\"");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(decrementTButton, gbc);
        incrementTButton = new JButton();
        incrementTButton.setHorizontalTextPosition(11);
        incrementTButton.setIcon(new ImageIcon(getClass().getResource("/gui/mainapp/next.png")));
        incrementTButton.setText("F4");
        incrementTButton.setToolTipText("Increment parameter \"t\"");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(incrementTButton, gbc);
        zoomOutButton = new JButton();
        zoomOutButton.setHorizontalTextPosition(10);
        zoomOutButton.setIcon(new ImageIcon(getClass().getResource("/gui/mainapp/zoom-out.png")));
        zoomOutButton.setText("F7");
        zoomOutButton.setToolTipText("Zoom out viewport");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(zoomOutButton, gbc);
        zoomInButton = new JButton();
        zoomInButton.setHorizontalTextPosition(11);
        zoomInButton.setIcon(new ImageIcon(getClass().getResource("/gui/mainapp/zoom-in.png")));
        zoomInButton.setText("F8");
        zoomInButton.setToolTipText("Zoom in viewport");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(zoomInButton, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 5;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(panel3, gbc);
        coordinateSystemCheckBox = new JCheckBox();
        coordinateSystemCheckBox.setText("coord. sys.");
        coordinateSystemCheckBox.setToolTipText("Shows coordinate system");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(coordinateSystemCheckBox, gbc);
        gridCheckBox = new JCheckBox();
        gridCheckBox.setText("grid");
        gridCheckBox.setToolTipText("Shows grid");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(gridCheckBox, gbc);
        aspectRatioCheckBox = new JCheckBox();
        aspectRatioCheckBox.setLabel("aspect ratio");
        aspectRatioCheckBox.setText("aspect ratio");
        aspectRatioCheckBox.setToolTipText("Shows aspect ratio");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(aspectRatioCheckBox, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        sidePanel.add(panel4, BorderLayout.CENTER);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel4.add(scrollPane1, BorderLayout.CENTER);
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Equations"));
        equationPad = new JTextArea();
        equationPad.setText("coords(5,1)\ny=\n");
        equationPad.putClientProperty("html.disable", Boolean.TRUE);
        scrollPane1.setViewportView(equationPad);
        errorLabel = new JLabel();
        errorLabel.setBackground(new Color(-13108));
        errorLabel.setEnabled(true);
        errorLabel.setForeground(new Color(-6737152));
        errorLabel.setOpaque(true);
        errorLabel.setText("<html>Please correct expression: <ul> <li>insert variable or constant</li> </ul>");
        panel4.add(errorLabel, BorderLayout.SOUTH);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new BorderLayout(0, 0));
        splitPane1.setRightComponent(panel5);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        panel5.add(panel6, BorderLayout.SOUTH);
        panel6.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Player"));
        timeSlider = new JSlider();
        timeSlider.setSnapToTicks(true);
        timeSlider.setValue(0);
        timeSlider.putClientProperty("JSlider.isFilled", Boolean.FALSE);
        timeSlider.putClientProperty("html.disable", Boolean.FALSE);
        timeSlider.putClientProperty("Slider.paintThumbArrowShape", Boolean.FALSE);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 100.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(timeSlider, gbc);
        playButton = new JButton();
        playButton.setHorizontalTextPosition(11);
        playButton.setIcon(new ImageIcon(getClass().getResource("/gui/mainapp/play.png")));
        playButton.setText(" F5");
        playButton.setToolTipText("Run time series of graphics by changing \"t\" from 0 to 1 ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(playButton, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new BorderLayout(0, 0));
        panel5.add(panel7, BorderLayout.CENTER);
        panel7.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Viewport"));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new BorderLayout(0, 0));
        panel8.setBackground(Color.white);
        panel7.add(panel8, BorderLayout.CENTER);
        panel8.add(equalViewport, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

    private class EquationUpdater implements DocumentListener, Runnable {
        private final EqualViewModel viewModel;

        public EquationUpdater(EqualViewModel viewModel) {
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
            viewListener.withDisabled(InterfacePart.EQUATION, this);
        }

        @Override
        public void run() {
            viewModel.setEquations(equationPad.getText());
        }
    }

    private class InterfaceUpdater implements InterfacePartVisitor {
        private final EqualViewModel viewModel;

        public InterfaceUpdater(EqualViewModel viewModel) {
            this.viewModel = viewModel;
        }

        @Override
        public void constants() {
            constantsLabel.setText(viewModel.getConstantsStatus());
        }

        @Override
        public void variables() {
            variablesLabel.setText(viewModel.getVariablesStatus());
        }

        @Override
        public void equation() {
            equationPad.setText(viewModel.getEquations());
        }

        @Override
        public void viewport() {
            try {
                Dimension dim = viewModel
                        .getViewportSize()
                        .toDimension();
                if (!dim.equals(equalViewport.getSize())) {
                    equalViewport.setSize(dim);
                }
                equalViewport.setViewportBounds(viewModel.getViewportBounds());
                equalViewport.setT(viewModel.getTAsVariable());
                equalViewport.setExpression(viewModel.getEquations());
                syntaxErrorDisplay.hide();

            } catch (ParsingException e) {
                syntaxErrorDisplay.show(e);
            }
        }

        @Override
        public void timeControl() {
            timeSlider.setMinimum(0);
            timeSlider.setMaximum(viewModel.getSteps());
            timeSlider.setValue(viewModel.getT());
        }
    }

    private class EqualAppPanelViewListener implements ViewListener {
        private Set<InterfacePart> disabled;

        public void withDisabled(InterfacePart disabledPart, Runnable runnable) {
            Set<InterfacePart> prevDisabled = disabled;
            disabled = EnumSet.of(disabledPart);
            runnable.run();
            disabled = prevDisabled;
        }

        @Override
        public void onUpdate(Set<InterfacePart> parts) {
            for (InterfacePart part : parts) {
                if (disabled != null && disabled.contains(part)) {
                    continue;
                }
                part.accept(new InterfaceUpdater(viewModel));
            }
        }

        @Override
        public void onPlayStateChange(PlayState state) {
            state.accept(player);
        }
    }

    private class TimeSliderUpdater implements ChangeListener, Runnable {

        @Override
        public void stateChanged(ChangeEvent e) {
            viewListener.withDisabled(InterfacePart.TIME_CONTROL, this);
        }

        @Override
        public void run() {
            viewModel.setT(timeSlider.getValue());
        }
    }

    private class ViewportSizeListener extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            Dimension dim = equalViewport.getSize();
            int width = (int) Math.round(dim.getWidth());
            int height = (int) Math.round(dim.getHeight());
            if (width < 0 || height < 0) {
                width = height = 0;
            }
            viewModel.setViewportSize(
                    new ViewportSize(width, height));
        }
    }

}
