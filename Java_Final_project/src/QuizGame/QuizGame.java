package QuizGame;

import javax.swing.SwingUtilities;
import ui.MainWindow;

public class QuizGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}

//host question panel(host's screen when players answering quesitons)