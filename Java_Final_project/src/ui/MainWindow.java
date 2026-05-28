package ui;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;
    
    private HostSetupPanel hostSetupPanel;
    private JoinPanel joinPanel;
    private LobbyPanel lobbyPanel;
    private int roomPin;
    
    
    public static final String SCREEN_HOST_SETUP = "HOST_SETUP";
    public static final String SCREEN_JOIN         = "JOIN";
    public static final String SCREEN_LOBBY         = "LOBBY";
    public static final String SCREEN_QUESTION        = "QUESTION";
    public static final String SCREEN_LEADERBOARD = "LEADERBOARD";
    public static final String SCREEN_WAITINGROOM= "WAITINGROOM";
    public static final String SCREEN_BET = "BET";

    
    public MainWindow() {
    	roomPin = 1000 + (int)(Math.random() * 9000);
    	System.out.println(roomPin);
        setTitle("QuizRush");
        setSize(500, 600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
//        hostSetupPanel   = new HostSetupPanel(this);
//        joinPanel        = new JoinPanel(this);
//        lobbyPanel       = new LobbyPanel(this);

        container.add(new HostSetupPanel(this, roomPin), SCREEN_HOST_SETUP);
        container.add(new JoinPanel(this, roomPin), SCREEN_JOIN);
        
        container.add(new LobbyPanel(roomPin), SCREEN_LOBBY);
        container.add(new QuestionPanel(), SCREEN_QUESTION);
        container.add(new LeaderboardPanel(), SCREEN_LEADERBOARD);
        container.add(new PlayerWaitingLobbyPanel("Sarah"), SCREEN_WAITINGROOM);
        //container.add(new BetPanel(this), SCREEN_BET); 
        
        ///change the pincode setting
        //container.add(new LobbyPanel(roomPin), SCREEN_LOBBY);
        //cardLayout.show(container, SCREEN_HOST_SETUP);
        //showScreen(SCREEN_LOBBY);
        showScreen(SCREEN_QUESTION);
        //showScreen(SCREEN_LEADERBOARD);
        //showScreen(SCREEN_WAITINGROOM);
        //showScreen(SCREEN_JOIN);
        
        BetPanel betPanel = new BetPanel(this);
        container.add(betPanel, SCREEN_BET);
        betPanel.setScore(1840); // test score for now
        
        //showScreen(SCREEN_BET);
                
        
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
