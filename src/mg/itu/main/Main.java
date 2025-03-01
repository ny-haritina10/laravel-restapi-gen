package mg.itu.main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import mg.itu.frame.LaravelCrudGenerator;

public class Main {
     public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LaravelCrudGenerator().setVisible(true);
        });
    }
}