package gui.mainapp;

import javax.swing.*;

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
    private JSlider playSlider;
    private JButton playButton;
    private JTextField stepsField;
    private JButton upButton;
    private JButton leftButton;
    private JButton downButton;
    private JButton rightButton;
    private JTextField upBoundField;
    private JTextField rightBoundField;
    private JTextField leftBoundField;
    private JTextField downBoundField;

    public static void main(String[] args) {
        JFrame frame = new JFrame("equal5");
        frame.setContentPane(new EqualMainframe().root);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1000, 600);
        frame.setVisible(true);
    }}
