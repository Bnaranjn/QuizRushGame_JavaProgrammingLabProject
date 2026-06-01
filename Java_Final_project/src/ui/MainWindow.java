package ui;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainWindow extends JFrame {
	

    private CardLayout cardLayout;
    private JPanel container;
    
    //panels
    private HostSetupPanel hostSetupPanel;
    private JoinPanel joinPanel;
    private LobbyPanel lobbyPanel;
    private int roomPin;
    
    //reveal panel components
    private QuestionPanel questionPanel;
    private RevealPanel revealPanel;
    private BetPanel betPanel;

    private int currentQuestion = 0; // 0 to 9
    private int playerScore = 0;
    private int lastSelectedAnswer = -1;

    
    public static final String SCREEN_REVEAL = "REVEAL";
    
    
    public static final String SCREEN_HOST_SETUP = "HOST_SETUP";
    public static final String SCREEN_JOIN       = "JOIN";
    public static final String SCREEN_LOBBY      = "LOBBY";
    public static final String SCREEN_QUESTION   = "QUESTION";
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
	    hostSetupPanel   = new HostSetupPanel(this, roomPin);
	    joinPanel        = new JoinPanel(this, roomPin);
        lobbyPanel       = new LobbyPanel(this, roomPin);

        container.add(hostSetupPanel, SCREEN_HOST_SETUP);
        container.add(joinPanel, SCREEN_JOIN);
        
        container.add(lobbyPanel, SCREEN_LOBBY);
        
        questionPanel = new QuestionPanel(this);
        container.add(questionPanel, SCREEN_QUESTION);
        
        revealPanel = new RevealPanel(this);
        container.add(revealPanel, SCREEN_REVEAL);
        
        betPanel = new BetPanel(this);
        container.add(betPanel, SCREEN_BET);
        
        container.add(new LeaderboardPanel(), SCREEN_LEADERBOARD);
        container.add(new PlayerWaitingLobbyPanel("Sarah"), SCREEN_WAITINGROOM);
        //container.add(new BetPanel(this), SCREEN_BET); 
        
 
        //container.add(new LobbyPanel(roomPin), SCREEN_LOBBY);
        cardLayout.show(container, SCREEN_HOST_SETUP);
        //showScreen(SCREEN_LOBBY);
        //showScreen(SCREEN_QUESTION);
        //showScreen(SCREEN_LEADERBOARD);
        //showScreen(SCREEN_WAITINGROOM);
        //showScreen(SCREEN_JOIN);
        
//        BetPanel betPanel = new BetPanel(this);
//        container.add(betPanel, SCREEN_BET);
//        betPanel.setScore(1840); // test score for now
        
        //showScreen(SCREEN_BET);
        
        add(container);
    }
    public void showScreen(String screenName) {
        cardLayout.show(container, screenName);
    }
    
    //Called by LobbyPanel "Start quiz" button
    public void loadCurrentQuestion() {
        String text      = QuestionBank.getText(currentQuestion);
        String[] options = QuestionBank.getOptions(currentQuestion);
        int total        = QuestionBank.getTotalQuestions();

        questionPanel.loadQuestion(text, options, currentQuestion + 1, total, playerScore);
        showScreen(SCREEN_QUESTION);
    }
 // Called by QuestionPanel when player clicks an answer
    public void submitAnswer(int selectedAnswer) {
        questionPanel.stopTimer();

        int correct      = QuestionBank.getCorrectIndex(currentQuestion);
        String[] options = QuestionBank.getOptions(currentQuestion);

        int pointsEarned = 0;
        if (selectedAnswer == correct) {
            pointsEarned = 500 + (questionPanel.getTimeLeft() * 33);
            playerScore += pointsEarned;
        }

        revealPanel.showResult(selectedAnswer, correct, options, pointsEarned);
        showScreen(SCREEN_REVEAL);
    }
    public void goToNextScreen() {
        currentQuestion++;

        // Bet round after Q3, Q6, Q9
        if (currentQuestion == 3 || currentQuestion == 6 || currentQuestion == 9) {
            betPanel.setScore(playerScore);
            betPanel.startTimer();
            showScreen(SCREEN_BET);
            return;
        }
        // All questions done
        if (currentQuestion >= QuestionBank.getTotalQuestions()) {
            showScreen(SCREEN_LEADERBOARD);
            return;
        }
        // Load next question
        loadCurrentQuestion();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }
    
}
