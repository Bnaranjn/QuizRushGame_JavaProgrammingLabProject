package ui;

import java.awt.CardLayout;
import java.util.*;
import javax.swing.*;
import network.*;
import QuizGame.*;

/**
 * MainWindow: Central application window using CardLayout for screens.
 *
 * Network protocol (all messages sent over TCP via DataUTF):
 *   JOIN|<name>              client → server  (player joins)
 *   PLAYER_LIST|a,b,c        server → all     (updated roster)
 *   START_GAME               server → all     (host pressed Start)
 *   QUESTION|<idx>           server → all     (next question index)
 *   BET_ROUND                server → all     (betting round starts)
 *   BET|<name>|<amt>|<mult>  client → server  (player's bet)
 *   ANSWER|<name>|<idx>      client → server  (player answered)
 *   SCORE_UPDATE|<name>|<score>  server → all
 *   LEADERBOARD|name:score,name:score,...  server → all
 *   GAME_OVER                server → all
 */
public class MainWindow extends JFrame implements MessageListener {

    // Screen name constants 
	public static final String SCREEN_HOST_SETUP  = "HOST_SETUP";
    public static final String SCREEN_JOIN         = "JOIN";
    public static final String SCREEN_LOBBY        = "LOBBY";
    public static final String SCREEN_WAITINGROOM  = "WAITINGROOM";
    public static final String SCREEN_QUESTION     = "QUESTION";
    public static final String SCREEN_REVEAL       = "REVEAL";
    public static final String SCREEN_BET          = "BET";
    public static final String SCREEN_LEADERBOARD  = "LEADERBOARD";

    //Layout 
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel container = new JPanel(cardLayout);

    //Panels 
    private final HostSetupPanel      hostSetupPanel;
    private final JoinPanel           joinPanel;
    private final LobbyPanel          lobbyPanel;
    private final PlayerWaitingLobbyPanel waitingPanel;
    private final QuestionPanel       questionPanel;
    private final RevealPanel         revealPanel;
    private final BetPanel            betPanel;
    private final LeaderboardPanel    leaderboardPanel;

    //Network 
    private QuizServer server;   // non-null only on the host machine
    private QuizClient client;
    // Backend player object for THIS client
    // All score and bet state is owned by this object, not by MainWindow.
    private Player localPlayer;
 

    //Game state 
    //login doesnt validitate roompin - may not be needed
    private final int roomPin = 1000 + (int)(Math.random() * 9000);
    private String myName = "Player";
    private boolean isHost = false;

    /** Score tracked locally for THIS window (host plays too if desired). */
    //private int myScore = 0;
    private int currentQuestionIndex = 0;
    
    //---------
    //BACKEND 
    //--------
    /** Bet state for the current bet round. */
    //private int pendingBetAmount     = 0;
    //private int pendingBetMultiplier = 1;
    //private boolean hasBet           = false;
    //private final Map<String, Integer> playerScores = new LinkedHashMap<>();
    
    //CONSTRUCTOR
    public MainWindow() {
        setTitle("QuizRush");
        setSize(500, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        hostSetupPanel  = new HostSetupPanel(this, roomPin);
        joinPanel       = new JoinPanel(this, roomPin);
        lobbyPanel      = new LobbyPanel(this, roomPin);
        waitingPanel    = new PlayerWaitingLobbyPanel(this);
        questionPanel   = new QuestionPanel(this);
        revealPanel     = new RevealPanel(this);
        betPanel        = new BetPanel(this);
        leaderboardPanel = new LeaderboardPanel(this);

        container.add(hostSetupPanel,  SCREEN_HOST_SETUP);
        container.add(joinPanel,       SCREEN_JOIN);
        container.add(lobbyPanel,      SCREEN_LOBBY);
        container.add(waitingPanel,    SCREEN_WAITINGROOM);
        container.add(questionPanel,   SCREEN_QUESTION);
        container.add(revealPanel,     SCREEN_REVEAL);
        container.add(betPanel,        SCREEN_BET);
        container.add(leaderboardPanel, SCREEN_LEADERBOARD);

        add(container);
        cardLayout.show(container, SCREEN_HOST_SETUP);
    }
    
    //-------------
   //NAVIGATION
    public void showScreen(String name) {
        cardLayout.show(container, name);
    }
    //HOSTING/JOINING --------------
    
    /** Called when host clicks "Open Lobby". Starts the server and joins as host client. */
    public void startHosting() {
        isHost = true;
        myName = "Host";
        localPlayer = new Player(myName);
        try {
            server = new QuizServer();
            server.startServer(5000);
            // Host also connects as a client so it receives broadcasts
            client = new QuizClient("localhost", 5000, this);
            client.send("JOIN|Host");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not start server: " + e.getMessage());
        }
    }

    /** Called when host clicks "Start Quiz" in LobbyPanel. */
    public void startQuizForEveryone() {
        if (server != null) {
            server.broadcast("QUESTION|0");
        }
    }

    /** Called from JoinPanel when player enters name and clicks Join. */
    public void joinGame(String name, String ip) {
        myName = (name == null || name.isBlank()) ? "Player" : name.trim();
        localPlayer = new Player(myName);
        waitingPanel.setMyName(myName);
        try {
            client = new QuizClient(ip, 5000, this);
            client.send("JOIN|" + myName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not connect: " + e.getMessage()
                + "\n\nMake sure the host's IP is correct and the game is open.");
        }
    }
    
    //QUESTION FLOW

    /** Loads question at given index, called when QUESTION|idx arrives. */
    public void loadQuestion(int idx) {
        currentQuestionIndex = idx;
        String   text    = QuestionBank.getText(idx);
        String[] options = QuestionBank.getOptions(idx);
        int      total   = QuestionBank.getTotalQuestions();
        questionPanel.loadQuestion(text, options, idx + 1, total, localPlayer.getScore());
        showScreen(SCREEN_QUESTION);
    }

    /**
     * Called by QuestionPanel when the player clicks an answer (or time runs out).
     * Sends ANSWER message and shows the reveal screen.
     */
    
    //-----------------------------------
    //this to be at the backend - 
    //-----------------------------
//    public void submitAnswer(int selectedAnswer) {
//        questionPanel.stopTimer();
//
//        int correct      = QuestionBank.getCorrectIndex(currentQuestionIndex);
//        String[] options = QuestionBank.getOptions(currentQuestionIndex);
//        int pointsEarned = 0;
//
//        if (selectedAnswer == correct) {
//            // Base 500 + up-to-660 speed bonus (20 s * 33 pts/s)
//            pointsEarned = 500 + (questionPanel.getTimeLeft() * 33);
//        }
//
//        // Apply pending bet result
//        if (hasBet && pendingBetAmount > 0) {
//            if (selectedAnswer == correct) {
//                pointsEarned += pendingBetAmount * (pendingBetMultiplier - 1);
//            } else {
//                pointsEarned -= pendingBetAmount;
//            }
//            hasBet = false;
//            pendingBetAmount     = 0;
//            pendingBetMultiplier = 1;
//        }
//
//        myScore = Math.max(0, myScore + pointsEarned);
//        playerScores.put(myName, myScore);
//
//        // Notify server of score
//        sendToServer("SCORE_UPDATE|" + myName + "|" + myScore);
//
//        revealPanel.showResult(selectedAnswer, correct, options,
//                               pointsEarned > 0 ? pointsEarned : 0,
//                               selectedAnswer == correct);
//        showScreen(SCREEN_REVEAL);
//    }
    //replacement in the ui page
    public void submitAnswer(int selectedAnswer) {
    	if(isHost) return; //dont take hosts answer

        questionPanel.stopTimer();
        int timeLeft= questionPanel.getTimeLeft();
        int      correct  = QuestionBank.getCorrectIndex(currentQuestionIndex);
        String[] options  = QuestionBank.getOptions(currentQuestionIndex);
        boolean  isCorrect = (selectedAnswer == correct);
        //passes the time to the player
        try {
            localPlayer.submitAnswer(selectedAnswer, timeLeft);  //store it in Player
        } catch (InvalidAnswerException e) {/* timeout -1 is valid, won't throw */ }

        //SCORING HAPPENS IN PLAYER
        //!!!! - RESOLVEANSWER FUNCTION
        int pointsEarned = localPlayer.resolveAnswer(isCorrect, localPlayer.getAnswerTimeLeft());
        
        //NOTIFYING THE SERVER
        sendToServer(
            "ANSWER|"
            + myName
            + "|"
            + selectedAnswer
            + "|"
            + timeLeft
        );
//        revealPanel.showResult(selectedAnswer, correct, options,
//                Math.max(0, pointsEarned), isCorrect);
        revealPanel.showResult(selectedAnswer, correct, options, pointsEarned,isCorrect);
        
        showScreen(SCREEN_REVEAL);
    }

    /**
     * Called by RevealPanel after its countdown, to go to the next screen.
     * Host decides what happens next by broadcasting.
     */
    public void goToNextScreen() {
        if (!isHost) return;   // Only the host drives game progression
        int total = QuestionBank.getTotalQuestions();
        
        
        //GAMEFLOW DECISION - BUILD NEXTEVENT IN GAMESESSION
        GameSession.NextEvent event = GameSession.getNextEvent(currentQuestionIndex, total);
        
        switch (event) {
        case BET_ROUND:
            server.broadcast("BET_ROUND|" + (currentQuestionIndex + 1));
            break;

        case GAME_OVER:
            // Server owns the scores and builds the leaderboard string
            server.broadcastLeaderboard();
            break;

        case NEXT_QUESTION:
            server.broadcast("QUESTION|" + (currentQuestionIndex + 1));
            break;
        }

//        int next = currentQuestionIndex + 1;
//
//        // Bet round after Q2, Q5, Q8 (every 3rd question, 0-indexed)
//        // i.e. after question indices 2, 5, 8
//        if (next <= QuestionBank.getTotalQuestions()
//                && (next == 3 || next == 6 || next == 9)) {
//            server.broadcast("BET_ROUND|" + next);
//            return;
//        }
//
//        if (next >= QuestionBank.getTotalQuestions()) {
//            // Collect leaderboard
//            broadcastLeaderboard();
//            return;
//        }
//
//        server.broadcast("QUESTION|" + next);
    }

    /** Host broadcasts LEADERBOARD message then GAME_OVER. */
    //
//    private void broadcastLeaderboard() {
//        StringBuilder sb = new StringBuilder();
//        playerScores.forEach((name, score) ->
//            sb.append(name).append(":").append(score).append(","));
//        if (sb.length() > 0) sb.setLength(sb.length() - 1);
//        server.broadcast("LEADERBOARD|" + sb);
//        server.broadcast("GAME_OVER");
//    }

    /** Called by BetPanel "Place Bet" button. */
//    public void placeBet(int amount, int multiplier) {
//    	
////        this.pendingBetAmount     = amount;
////        this.pendingBetMultiplier = multiplier;
////        this.hasBet               = true;
//        sendToServer("BET|" + myName + "|" + amount + "|" + multiplier);
//    }
    public void placeBet(int amount, int multiplier) {
        try {
            localPlayer.setBet(amount, multiplier);
        } catch (InvalidBetException e) {
            JOptionPane.showMessageDialog(this, "Invalid bet: " + e.getMessage());
            return;
        }
        sendToServer("BET|" + myName + "|" + amount + "|" + multiplier);
    }
 
    /** Called by BetPanel after bet is placed or skipped, so host advances. */
    public void betDone(int nextQuestionIndex) {
        if (isHost) {
            server.broadcast("QUESTION|" + nextQuestionIndex);
        }
    }


    //MessageListener 
    @Override
    public void onMessageReceived(String message) {
        SwingUtilities.invokeLater(() -> handleServerMessage(message));
    }

    private void handleServerMessage(String message) {
        System.out.println("[MSG] " + message);

        if (message.startsWith("PLAYER_LIST|")) {
            // Update lobby and waiting room with current player list
            String csv = message.substring("PLAYER_LIST|".length());
            String[] names = csv.isEmpty() ? new String[0] : csv.split(",");
            lobbyPanel.setPlayerList(names);
            waitingPanel.setPlayerList(names);

        } else if (message.startsWith("QUESTION|")) {
            int idx = Integer.parseInt(message.substring("QUESTION|".length()).trim());
            loadQuestion(idx);

        } else if (message.startsWith("BET_ROUND|")) {
            int nextIdx = Integer.parseInt(message.substring("BET_ROUND|".length()).trim());
            betPanel.startBetRound(localPlayer.getScore(), nextIdx);
            showScreen(SCREEN_BET);

        } else if (message.startsWith("SCORE_UPDATE|")) {
//            // e.g. SCORE_UPDATE|Alice|1340
//            String[] parts = message.split("\\|");
//            if (parts.length == 3) {
//                String name  = parts[1];
//                int    score = Integer.parseInt(parts[2]);
//                playerScores.put(name, score);
//            }
        	// Server re-broadcasts this; no client-side score map needed anymore
            // (LeaderboardPanel gets its data from the LEADERBOARD message)

        } else if (message.startsWith("LEADERBOARD|")) {
            String csv = message.substring("LEADERBOARD|".length());
            Map<String, Integer> scores = new LinkedHashMap<>();
            for (String entry : csv.split(",")) {
                String[] kv = entry.split(":");
                if (kv.length == 2) {
                    try { scores.put(kv[0], Integer.parseInt(kv[1])); }
                    catch (NumberFormatException ignored) {}
                }
            }
            leaderboardPanel.showLeaderboard(scores, myName);
            showScreen(SCREEN_LEADERBOARD);

        } else if (message.equals("GAME_OVER")) {
            // Already handled by LEADERBOARD message above
        }
    }

    //  Utility 
    private void sendToServer(String msg) {
        if (client != null) {
            try { client.send(msg); }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    public String getMyName()  { return myName; }
    //SCORE READ FROM THE BACKEND PLAYER OBJECT
    public int    getMyScore() { return localPlayer != null ? localPlayer.getScore() : 0; }
    public boolean isHost()    { return isHost; }

    //Entry point 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
