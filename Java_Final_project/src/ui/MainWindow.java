package ui;

import javax.swing.*;
import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import network.*;

public class MainWindow extends JFrame implements MessageListener {
    public static final String SCREEN_SETUP = "SETUP_VIEW";
    public static final String SCREEN_JOIN = "JOIN_VIEW";
    public static final String SCREEN_LOBBY = "LOBBY_VIEW";
    public static final String SCREEN_HOST_WAITING = "HOST_WAITING_VIEW";
    public static final String SCREEN_PLAYER_WAITING = "PLAYER_WAITING_VIEW";
    public static final String SCREEN_QUESTION = "QUESTION_VIEW";
    public static final String SCREEN_REVEAL = "REVEAL_VIEW";
    public static final String SCREEN_BET = "BET_VIEW";
    public static final String SCREEN_LEADERBOARD = "LEADERBOARD_VIEW";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardContainer = new JPanel(cardLayout);

    private HostSetupPanel setupPanel;
    private JoinPanel joinPanel;
    private LobbyPanel lobbyPanel;
    private HostWaitingPanel hostWaitingPanel;
    private PlayerWaitingPanel playerWaitingPanel;
    private QuestionPanel questionPanel;
    private RevealPanel revealPanel;
    private LeaderboardPanel leaderboardPanel;
    private BetPanel betPanel;

    private QuizServer localServerInstance;
    private QuizClient networkClientLink;
    
    private String playerNickname = "Unknown";
    private boolean isHostNode = false;
    private int connectedPlayerCount = 0;
    
    private static final int DEFAULT_FALLBACK_PORT = 5000; 

    public MainWindow() {
        setTitle("QuizRush Framework Engine");
        setSize(480, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Intercept manually
        setLocationRelativeTo(null);

        // Frame close handler to alert connected clients or disconnect from server cleanly
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowCloseCleanup();
            }
        });

        setupPanel = new HostSetupPanel(this, DEFAULT_FALLBACK_PORT);
        joinPanel = new JoinPanel(this);
        lobbyPanel = new LobbyPanel(this);
        hostWaitingPanel = new HostWaitingPanel(this);
        playerWaitingPanel = new PlayerWaitingPanel(this);
        questionPanel = new QuestionPanel(this);
        revealPanel = new RevealPanel(this);
        betPanel = new BetPanel(this);
        leaderboardPanel = new LeaderboardPanel(this);

        cardContainer.add(setupPanel, SCREEN_SETUP);
        cardContainer.add(joinPanel, SCREEN_JOIN);
        cardContainer.add(lobbyPanel, SCREEN_LOBBY);
        cardContainer.add(hostWaitingPanel, SCREEN_HOST_WAITING);
        cardContainer.add(playerWaitingPanel, SCREEN_PLAYER_WAITING);
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

    public void startHosting(int selectedPort) {
        this.isHostNode = true;
        this.playerNickname = "Host";
        try {
            localServerInstance = new QuizServer();
            localServerInstance.startServer(selectedPort);

            networkClientLink = new QuizClient("localhost", selectedPort, this);
            networkClientLink.send("JOIN|Host");
            
            lobbyPanel.setRoomPortDisplay(selectedPort);
            lobbyPanel.setHostControls(true);
            showScreen(SCREEN_LOBBY);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to initialize server loop: " + e.getMessage(), "Server Bind Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void connectToGameLobby(String name, String ip, int port) {
        if (name.equalsIgnoreCase("Host")) {
            JOptionPane.showMessageDialog(this, "'Host' is a reserved administrative handle.", "Identity Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.isHostNode = false;
        this.playerNickname = name;
        try {
            networkClientLink = new QuizClient(ip, port, this);
            networkClientLink.send("JOIN|" + playerNickname);
            
            lobbyPanel.setRoomPortDisplay(port);
            lobbyPanel.setHostControls(false);
            showScreen(SCREEN_LOBBY);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Remote destination socket links refused connection.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void hostLoadAndStartQuiz(String filename) {
        if (!isHostNode) return;
        
        // Block starting the quiz game if no actual structural contestants exist yet
        if (connectedPlayerCount <= 0) {
            JOptionPane.showMessageDialog(this, "Cannot initialize game configuration: At least one player client must be connected to the lobby.", "Empty Room Alert", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (networkClientLink != null) {
            networkClientLink.send("START_GAME|" + filename);
        }
    }

    public void submitPlayerAnswer(int chosenIndex, int remainingTimeSeconds) {
        if (isHostNode) return;
        if (networkClientLink != null) {
            networkClientLink.send("ANSWER|" + playerNickname + "|" + chosenIndex + "|" + remainingTimeSeconds);
            playerWaitingPanel.setMessage("RESPONSE LOCKED IN", "Waiting for other players to finish computing...");
            showScreen(SCREEN_PLAYER_WAITING);
        }
    }

    public void submitPlayerBet(int pointsWagered, int selectedMultiplier) {
        if (isHostNode) return;
        if (networkClientLink != null) {
            networkClientLink.send("BET|" + playerNickname + "|" + pointsWagered + "|" + selectedMultiplier);
            playerWaitingPanel.setMessage("BET PLACED", "Waiting for other players to lock in their risk allocations...");
            showScreen(SCREEN_PLAYER_WAITING);
        }
    }

    public void hostRequestsNextPhase() {
        if (isHostNode && networkClientLink != null) {
            networkClientLink.send("REQUEST_NEXT_PHASE");
        }
    }

    private void handleWindowCloseCleanup() {
        if (isHostNode && localServerInstance != null) {
            // Signal server to announce an authoritative disconnect broadcast to all clients
            localServerInstance.broadcast("SERVER_SHUTDOWN");
            localServerInstance.stopServer();
        } else if (networkClientLink != null) {
            // Send explicit disconnect notice so server updates player counts dynamically
            networkClientLink.send("LEAVE|" + playerNickname);
            networkClientLink.disconnect();
        }
        System.exit(0);
    }

    @Override
    public void onMessageReceived(String message) {
        String[] parts = message.split("\\|");
        String header = parts[0];

        switch (header) {
            case "SERVER_SHUTDOWN":
                networkClientLink.disconnect();
                JOptionPane.showMessageDialog(this, "The Host has terminated the game session room. Closing application window.", "Session Closed", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                break;

            case "PLAYER_LIST":
                List<String> list = Arrays.asList(parts[1].split(","));
                // Compute non-host player metrics count safely
                int playersOnlyCount = 0;
                for (String name : list) {
                    if (!name.equalsIgnoreCase("Host")) playersOnlyCount++;
                }
                this.connectedPlayerCount = playersOnlyCount;
                lobbyPanel.updatePlayerList(list);
                break;

            case "START_GAME":
                showScreen(isHostNode ? SCREEN_HOST_WAITING : SCREEN_PLAYER_WAITING);
                break;

            case "QUESTION":
                if (isHostNode) {
                    hostWaitingPanel.setMessage("ROUND PROGRESS TRACKER", "Players are currently answering questions...");
                    showScreen(SCREEN_HOST_WAITING);
                } else {
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
                }
                break;

            case "REVEAL":
                int correctChoiceIdx = Integer.parseInt(parts[1]);
                Map<String, Integer> currentScores = parseStandingsMap(parts.length > 2 ? parts[2] : "");

                revealPanel.showResults(correctChoiceIdx, currentScores, playerNickname, isHostNode);
                showScreen(SCREEN_REVEAL);
                break;

            case "BET_ROUND":
                if (isHostNode) {
                    hostWaitingPanel.setMessage("BET PHASE MONITOR", "Players are adjusting wager parameters...");
                    showScreen(SCREEN_HOST_WAITING);
                } else {
                    int targetNextIdx = Integer.parseInt(parts[1]);
                    Map<String, Integer> scoresForBet = parseStandingsMap(parts[2]);
                    int selfCurrentScore = scoresForBet.getOrDefault(playerNickname, 0);

                    SwingUtilities.invokeLater(() -> {
                        betPanel.startBetRound(selfCurrentScore, targetNextIdx);
                        showScreen(SCREEN_BET);
                    });
                }
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
        if (balancePayload.trim().isEmpty()) return generatedMap;
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