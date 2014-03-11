package gui.mainapp;

import gui.mainapp.viewmodel.EqualViewModel;

import javax.swing.*;
import java.util.Arrays;

/**
 * User: Oleksiy Pylypenko
 * At: 3/20/13  2:08 PM
 */
public class EqualApp {
    private final EqualViewModel model = new EqualViewModel();
    private final EqualAppPanel panel = new EqualAppPanel(model);
    private final JFrame frame = new JFrame("equal5");

    public static void main(String[] args) {
        EqualApp app = new EqualApp();
        app.show();
    }

    public EqualApp() {
        initGUI();
    }

    private void initGUI() {
        frame.setIconImages(
                Arrays.asList(
                        new ImageIcon(getClass().getResource("icon16x16.png")).getImage(),
                        new ImageIcon(getClass().getResource("icon32x32.png")).getImage(),
                        new ImageIcon(getClass().getResource("icon64x64.png")).getImage(),
                        new ImageIcon(getClass().getResource("icon256x256.png")).getImage()
                        )
        );
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);

        panel.beAContentPaneOf(frame);
    }

    private void show() {
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
