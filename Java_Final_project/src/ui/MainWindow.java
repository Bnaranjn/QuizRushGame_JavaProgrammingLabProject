package ui;

import javax.swing.*;
import java.awt.CardLayout;
import java.util.*;
import network.*;

public class MainWindow extends JFrame implements MessageListener {
    public static final String SCREEN_SETUP = "SETUP_VIEW";
    public static final String SCREEN_JOIN = "JOIN_VIEW";
    public static final String SCREEN_LOBBY = "LOBBY_VIEW";
    public static final String SCREEN_WAITING = "WAITING_VIEW";
    public static final String SCREEN_QUESTION = "QUESTION_VIEW";
    public static final String SCREEN_REVEAL = "REVEAL_VIEW";
    public static final String SCREEN_BET = "BET_VIEW";
    public static final String SCREEN_LEADERBOARD = "LEADERBOARD_VIEW";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardContainer = new JPanel(cardLayout);

    private HostSetupPanel setupPanel;
    private JoinPanel joinPanel;
    private LobbyPanel lobbyPanel;
    private PlayerWaitingLobbyPanel waitingPanel;
    private QuestionPanel questionPanel;
    private RevealPanel revealPanel;
    private LeaderboardPanel leaderboardPanel;
    private BetPanel betPanel;

    private QuizServer localServerInstance;
    private QuizClient networkClientLink;
    
    private String playerNickname = "Unknown";
    private boolean isHostNode = false;
    private final int networkActivePort = 5000;

    public MainWindow() {
        setTitle("QuizRush Framework Engine");
        setSize(480, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setupPanel = new HostSetupPanel(this, networkActivePort);
        joinPanel = new JoinPanel(this);
        lobbyPanel = new LobbyPanel(this, networkActivePort);
        waitingPanel = new PlayerWaitingLobbyPanel(this);
        questionPanel = new QuestionPanel(this);
        revealPanel = new RevealPanel(this);
        betPanel = new BetPanel(this);
        leaderboardPanel = new LeaderboardPanel(this);

        cardContainer.add(setupPanel, SCREEN_SETUP);
        cardContainer.add(joinPanel, SCREEN_JOIN);
        cardContainer.add(lobbyPanel, SCREEN_LOBBY);
        cardContainer.add(waitingPanel, SCREEN_WAITING);
        cardContainer.add(questionPanel, SCREEN_QUESTION);
        cardContainer.add(revealPanel, SCREEN_REVEAL);
        cardContainer.add(betPanel, SCREEN_BET);
        cardContainer.add(leaderboardPanel, SCREEN_LEADERBOARD);

        add(cardContainer);
        showScreen(SCREEN_SETUP);
    }

    public void showScreen(String screenKey) {
        SwingUtilities.invokeLater(() -> cardLayout.show(cardContainer, screenKey));
    }

    public void startHosting() {
        this.isHostNode = true;
        this.playerNickname = "Host";
        try {
            localServerInstance = new QuizServer();
            localServerInstance.startServer(networkActivePort);

            networkClientLink = new QuizClient("localhost", networkActivePort, this);
            networkClientLink.send("JOIN|Host");
            
            lobbyPanel.setHostControls(true);
            showScreen(SCREEN_LOBBY);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to initialize server loop binding: " + e.getMessage(), "Server Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void connectToGameLobby(String name, String ip, int port) {
        this.isHostNode = false;
        this.playerNickname = name;
        try {
            networkClientLink = new QuizClient(ip, port, this);
            networkClientLink.send("JOIN|" + playerNickname);
            
            lobbyPanel.setHostControls(false);
            showScreen(SCREEN_LOBBY);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Remote destination socket links refused connection: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void hostLoadAndStartQuiz(String filename) {
        if (isHostNode && networkClientLink != null) {
            networkClientLink.send("START_GAME|" + filename);
        }
    }

    public void submitPlayerAnswer(int chosenIndex, int remainingTimeSeconds) {
        if (networkClientLink != null) {
            networkClientLink.send("ANSWER|" + playerNickname + "|" + chosenIndex + "|" + remainingTimeSeconds);
            showScreen(SCREEN_WAITING);
        }
    }

    public void submitPlayerBet(int pointsWagered, int selectedMultiplier) {
        if (networkClientLink != null) {
            networkClientLink.send("BET|" + playerNickname + "|" + pointsWagered + "|" + selectedMultiplier);
            showScreen(SCREEN_WAITING);
        }
    }

    public void hostRequestsNextPhase() {
        if (isHostNode && networkClientLink != null) {
            networkClientLink.send("REQUEST_NEXT_PHASE");
        }
    }

    @Override
    public void onMessageReceived(String message) {
        String[] parts = message.split("\\|");
        String header = parts[0];

        switch (header) {
            case "PLAYER_LIST":
                List<String> list = Arrays.asList(parts[1].split(","));
                lobbyPanel.updatePlayerList(list);
                break;

            case "START_GAME":
                // All clients cleanly sync visual boundaries inside the Event Dispatch Thread
                showScreen(SCREEN_WAITING);
                break;

            case "QUESTION":
                // Layout: QUESTION|index|text|a|b|c|d
                String qText = parts[2];
                String a = parts[3];
                String b = parts[4];
                String c = parts[5];
                String d = parts[6];

                SwingUtilities.invokeLater(() -> {
                    questionPanel.setQuestionText(qText);
                    questionPanel.setOptions(a, b, c, d);
                    showScreen(SCREEN_QUESTION);
                    questionPanel.startTimer(20);
                });
                break;

            case "REVEAL":
                // Layout: REVEAL|correctIndex|name1:score1,name2:score2...
                int correctChoiceIdx = Integer.parseInt(parts[1]);
                Map<String, Integer> currentScores = parseStandingsMap(parts[2]);

                revealPanel.showResults(correctChoiceIdx, currentScores, playerNickname, isHostNode);
                showScreen(SCREEN_REVEAL);
                break;

            case "BET_ROUND":
                // Layout: BET_ROUND|nextQuestionIndex|name1:score1,name2:score2...
                int targetNextIdx = Integer.parseInt(parts[1]);
                Map<String, Integer> scoresForBet = parseStandingsMap(parts[2]);
                int selfCurrentScore = scoresForBet.getOrDefault(playerNickname, 0);

                SwingUtilities.invokeLater(() -> {
                    betPanel.startBetRound(selfCurrentScore, targetNextIdx);
                    showScreen(SCREEN_BET);
                });
                break;

            case "GAME_OVER":
                Map<String, Integer> terminalScores = parseStandingsMap(parts[1]);
                leaderboardPanel.displayFinalStandings(terminalScores, playerNickname);
                showScreen(SCREEN_LEADERBOARD);
                break;
        }
    }

    private Map<String, Integer> parseStandingsMap(String balancePayload) {
        Map<String, Integer> generatedMap = new LinkedHashMap<>();
        String[] nodes = balancePayload.split(",");
        for (String pair : nodes) {
            if (!pair.trim().isEmpty()) {
                String[] kv = pair.split(":");
                generatedMap.put(kv[0], Integer.parseInt(kv[1]));
            }
        }
        return generatedMap;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}