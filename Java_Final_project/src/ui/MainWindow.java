package ui;

import javax.swing.*;
import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import network.*;

public class MainWindow extends JFrame implements MessageListener {

    // Screen name constants used to switch between views in the CardLayout
    public static final String SCREEN_SETUP = "SETUP_VIEW";
    public static final String SCREEN_JOIN = "JOIN_VIEW";
    public static final String SCREEN_LOBBY = "LOBBY_VIEW";
    public static final String SCREEN_HOST_WAITING = "HOST_WAITING_VIEW";
    public static final String SCREEN_PLAYER_WAITING = "PLAYER_WAITING_VIEW";
    public static final String SCREEN_QUESTION = "QUESTION_VIEW";
    public static final String SCREEN_REVEAL = "REVEAL_VIEW";
    public static final String SCREEN_BET = "BET_VIEW";
    public static final String SCREEN_LEADERBOARD = "LEADERBOARD_VIEW";

    // CardLayout and its container panel � all screens live inside cardContainer
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardContainer = new JPanel(cardLayout);

    // All individual screen panels
    private HostSetupPanel setupPanel;
    private JoinPanel joinPanel;
    private LobbyPanel lobbyPanel;
    private HostWaitingPanel hostWaitingPanel;
    private PlayerWaitingPanel playerWaitingPanel;
    private QuestionPanel questionPanel;
    private RevealPanel revealPanel;
    private LeaderboardPanel leaderboardPanel;
    private BetPanel betPanel;

    // Network layer references � server is only created when this player is the host
    private QuizServer localServerInstance;
    private QuizClient networkClientLink;

    // Basic session state tracking
    private String playerNickname = "Unknown";
    private boolean isHostNode = false;        
    private int connectedPlayerCount = 0;      

    // These hold config values chosen before the game actually starts
    private String pendingQuestionFile = "general.txt";
    private int pendingPort = 5000;

    // Default port to use if nothing else is specified
    private static final int DEFAULT_FALLBACK_PORT = 5000;

    // Screen constant for the question set picker (defined separately since it was added later)
    public static final String SCREEN_QUESTION_PICKER = "QUESTION_PICKER_VIEW";

    private QuestionSetPickerPanel questionSetPickerPanel;

  
    public MainWindow() {
        setTitle("QuizRush");
        setSize(480, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        setLocationRelativeTo(null);

        // Intercept the window close button so we can cleanly disconnect before exiting
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowCloseCleanup();
            }
        });

        // Create all the screen panels
        setupPanel = new HostSetupPanel(this, DEFAULT_FALLBACK_PORT);
        joinPanel = new JoinPanel(this);
        lobbyPanel = new LobbyPanel(this);
        hostWaitingPanel = new HostWaitingPanel(this);
        playerWaitingPanel = new PlayerWaitingPanel(this);
        questionPanel = new QuestionPanel(this);
        revealPanel = new RevealPanel(this);
        betPanel = new BetPanel(this);
        leaderboardPanel = new LeaderboardPanel(this);
        questionSetPickerPanel = new QuestionSetPickerPanel(this);

        // Register each panel with the CardLayout under its respective screen key
        cardContainer.add(setupPanel, SCREEN_SETUP);
        cardContainer.add(joinPanel, SCREEN_JOIN);
        cardContainer.add(lobbyPanel, SCREEN_LOBBY);
        cardContainer.add(hostWaitingPanel, SCREEN_HOST_WAITING);
        cardContainer.add(playerWaitingPanel, SCREEN_PLAYER_WAITING);
        cardContainer.add(questionPanel, SCREEN_QUESTION);
        cardContainer.add(revealPanel, SCREEN_REVEAL);
        cardContainer.add(betPanel, SCREEN_BET);
        cardContainer.add(leaderboardPanel, SCREEN_LEADERBOARD);
        cardContainer.add(questionSetPickerPanel, SCREEN_QUESTION_PICKER);

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

            // Connect to our own server as a client so message flow is consistent
            networkClientLink = new QuizClient("localhost", selectedPort, this);
            networkClientLink.send("JOIN|Host");

            lobbyPanel.setRoomPortDisplay(selectedPort);
            lobbyPanel.setHostControls(true); // Show host-only controls like the start button
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
            lobbyPanel.setHostControls(false); // Hide host controls for regular players
            showScreen(SCREEN_LOBBY);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Remote destination socket links refused connection.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    public void hostLoadAndStartQuiz(String filename) {
        if (!isHostNode) return;

        // Don't allow starting if there are no players besides the host
        if (connectedPlayerCount <= 0) {
            JOptionPane.showMessageDialog(this, "Cannot initialize game configuration: At least one player client must be connected to the lobby.", "Empty Room Alert", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (networkClientLink != null) {
            networkClientLink.send("START_GAME|" + filename);
        }
    }

    // Getters and setters for pre-game config values
    public void setPendingPort(int port) { this.pendingPort = port; }
    public int getPendingPort() { return pendingPort; }
    public void setPendingQuestionFile(String file) { this.pendingQuestionFile = file; }
    public String getPendingQuestionFile() { return pendingQuestionFile; }

   
    public void submitPlayerAnswer(int chosenIndex, int remainingTimeSeconds) {
        if (isHostNode) return; // Host doesn't answer questions
        if (networkClientLink != null) {
            networkClientLink.send("ANSWER|" + playerNickname + "|" + chosenIndex + "|" + remainingTimeSeconds);
            playerWaitingPanel.setMessage("RESPONSE LOCKED IN", "Waiting for other players to finish computing...");
            showScreen(SCREEN_PLAYER_WAITING);
        }
    }

  
    public void submitPlayerBet(int pointsWagered, int selectedMultiplier) {
        if (isHostNode) return; // Host doesn't participate in betting
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
            // Notify all connected clients that the server is going down
            localServerInstance.broadcast("SERVER_SHUTDOWN");
            localServerInstance.stopServer();
        } else if (networkClientLink != null) {
            // Let the server know this player is leaving gracefully
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
                // Host closed the room � disconnect and inform the player
                networkClientLink.disconnect();
                JOptionPane.showMessageDialog(this, "The Host has terminated the game session room. Closing application window.", "Session Closed", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                break;

            case "PLAYER_LIST":
                List<String> list = Arrays.asList(parts[1].split(","));
                // Count only actual players, not the host entry
                int playersOnlyCount = 0;
                for (String name : list) {
                    if (!name.equalsIgnoreCase("Host")) playersOnlyCount++;
                }
                this.connectedPlayerCount = playersOnlyCount;
                lobbyPanel.updatePlayerList(list);
                break;

            case "START_GAME":
                // Host sees a passive waiting screen; players see a "get ready" screen
                showScreen(isHostNode ? SCREEN_HOST_WAITING : SCREEN_PLAYER_WAITING);
                break;

            case "QUESTION":
                if (isHostNode) {
                    // Host just monitors � doesn't answer
                    hostWaitingPanel.setMessage("ROUND PROGRESS TRACKER", "Players are currently answering questions...");
                    showScreen(SCREEN_HOST_WAITING);
                } else {
                    // Parse the question and its four answer options from the message
                    String qText = parts[2];
                    String a = parts[3];
                    String b = parts[4];
                    String c = parts[5];
                    String d = parts[6];

                    SwingUtilities.invokeLater(() -> {
                        questionPanel.setQuestionText(qText);
                        questionPanel.setOptions(a, b, c, d);
                        showScreen(SCREEN_QUESTION);
                        questionPanel.startTimer(20); // 20 second countdown per question
                    });
                }
                break;

            case "REVEAL":
                //int correctChoiceIdx = Integer.parseInt(parts[1]);
                String correctText = parts[1];
                Map<String, Integer> currentScores = parseStandingsMap(parts.length > 2 ? parts[2] : "");

                // Show who was right and updated scores
                revealPanel.showResults(correctText, currentScores, playerNickname, isHostNode);
                showScreen(SCREEN_REVEAL);
                break;

            case "BET_ROUND":
                if (isHostNode) {
                    // Host just watches while players place their bets
                    hostWaitingPanel.setMessage("BET PHASE MONITOR", "Players are adjusting wager parameters...");
                    showScreen(SCREEN_HOST_WAITING);
                } else {
                    // Pull the next question index and current scores to set up the bet UI
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
                // Game finished � show the final leaderboard with everyone's scores
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
}