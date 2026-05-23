package ui;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;
    
    public static final String SCREEN_HOST_SETUP = "HOST_SETUP";
    public static final String SCREEN_JOIN         = "JOIN";
    
    public MainWindow() {
    	

        setTitle("QuizRush");
        setSize(500, 600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        container.add(new HostSetupPanel(), SCREEN_HOST_SETUP);
        container.add(new JoinPanel(), SCREEN_JOIN);
        //cardLayout.show(container, SCREEN_HOST_SETUP);
        showScreen(SCREEN_JOIN);
        container.add(new JoinPanel(),        SCREEN_JOIN);
        
        
        add(container);
    }
    public void showScreen(String screenName) {
        cardLayout.show(container, screenName);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });

    }
}
