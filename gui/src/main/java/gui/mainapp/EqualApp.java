package gui.mainapp;

import javax.swing.*;

/**
 * User: Oleksiy Pylypenko
 * At: 3/20/13  2:08 PM
 */
public class EqualApp {
    public static void main(String[] args) {
        switchToNativeLAF();

        EqualApp app = new EqualApp();
        app.showGUI();
    }

    private void showGUI() {
        JFrame frame = new JFrame("equal5");
        EqualViewModel model = new EqualViewModel();

        new EqualAppPanel(model).beAContentPaneOf(frame);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void switchToNativeLAF() {
        // Native L&F
        try {
            String systemLF = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(systemLF);
        } catch (Exception e) {
            System.err.println("Unable to set native look and feel: " + e);
        }
    }

}
