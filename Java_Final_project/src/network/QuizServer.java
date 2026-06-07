package network;

import QuizGame.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class QuizServer {
    
    // listens for incoming connections
    private ServerSocket serverSocket;
    
    // thread that accepts new clients
    private Thread acceptThread;
    
    // all currently connected clients
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    
    // stores players by username
    private final Map<String, Player> playerRoster = new ConcurrentHashMap<>();
    
    // quiz currently being played
    private Quiz activeQuiz;
    
    // current question position
    private int currentQuestionIdx = -1;
    
    // tracks answer submissions
    private int questionsAnsweredCount = 0;
    
    // tracks bet submissions
    private int betsPlacedCount = 0;

    public void startServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        // accept clients in a separate thread
        acceptThread = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    // wait for a client connection
                    Socket socket = serverSocket.accept();

                    // create handler for new client
                    ClientHandler handler = new ClientHandler(socket, this);
                    clients.add(handler);

                    // start client thread
                    handler.start();
                } catch (IOException e) {
                    break;
                }
            }
        });

        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    private int getActivePlayerCount() {
        int count = 0;

        // host should not count as a player
        for (String name : playerRoster.keySet()) {
            if (!name.equalsIgnoreCase("Host")) {
                count++;
            }
        }
        return count;
    }

    public synchronized void handleMessage(String message, ClientHandler sender) {

        // split incoming packet into parts
        String[] tokens = message.split("\\|");
        String command = tokens[0];

        switch (command) {

            case "JOIN":
                String joinName = tokens[1];

                // add player if name not already used
                if (!playerRoster.containsKey(joinName)) {
                    Player newPlayer = new Player(joinName);

                    // special handling for host
                    if (joinName.equalsIgnoreCase("Host")) {
                        newPlayer.setHost(true);
                    }

                    playerRoster.put(joinName, newPlayer);
                }

                broadcastPlayerRoster();
                break;

            case "LEAVE":
                String leaveName = tokens[1];

                // remove disconnected player
                playerRoster.remove(leaveName);
                broadcastPlayerRoster();

                //re-verifying submission limits in case a player left mid-round
                int remainingPlayers = getActivePlayerCount();

                if (remainingPlayers > 0) {

                    // finish answer phase if everyone remaining answered
                    if (questionsAnsweredCount >= remainingPlayers) {
                        evaluateRoundAnswers();
                    }

                    // finish betting phase if everyone remaining bet
                    if (betsPlacedCount >= remainingPlayers) {
                        betsPlacedCount = 0;
                        advanceToNextQuestion();
                    }
                }
                break;

            case "START_GAME":
                String targetFile = tokens.length > 1 ? tokens[1] : "quiz.txt";

                // load quiz from selected file
                activeQuiz = new Quiz();
                activeQuiz.loadQuizFromFile(targetFile);

                // reset scores before starting
                for (Player p : playerRoster.values()) {
                    p.setScore(0);
                }

                broadcast("START_GAME");
                advanceToNextQuestion();
                break;

            case "ANSWER":
                String answeringPlayer = tokens[1];
                int answerIndex = Integer.parseInt(tokens[2]);
                int elapsedBonus = Integer.parseInt(tokens[3]);

                // host cannot answer questions
                if (answeringPlayer.equalsIgnoreCase("Host")) return;

                Player pAnswer = playerRoster.get(answeringPlayer);

                if (pAnswer != null) {
                    pAnswer.setSelectedAnswer(answerIndex);
                    pAnswer.setAnswerTimeLeft(elapsedBonus);
                }

                questionsAnsweredCount++;

                // reveal results once everyone answered
                if (questionsAnsweredCount >= getActivePlayerCount()) {
                    evaluateRoundAnswers();
                }
                break;

            case "BET":
                String bettingPlayer = tokens[1];
                int wagerAmount = Integer.parseInt(tokens[2]);
                int multValue = Integer.parseInt(tokens[3]);

                // host doesn't place bets
                if (bettingPlayer.equalsIgnoreCase("Host")) return;

                Player pBet = playerRoster.get(bettingPlayer);

                if (pBet != null) {
                    pBet.placeBet(wagerAmount, multValue);
                }

                betsPlacedCount++;

                // continue when all bets are received
                if (betsPlacedCount >= getActivePlayerCount()) {
                    betsPlacedCount = 0;
                    advanceToNextQuestion();
                }
                break;

            case "REQUEST_NEXT_PHASE":

                // determine next stage of game
                int totalCount = activeQuiz.getTotalQuestions();
                GameSession.NextEvent event =
                        GameSession.getNextEvent(currentQuestionIdx, totalCount);

                if (event == GameSession.NextEvent.BET_ROUND) {
                    broadcastBetRoundPhase();

                } else if (event == GameSession.NextEvent.NEXT_QUESTION) {
                    advanceToNextQuestion();

                } else {
                    broadcastFinalLeaderboard();
                }
                break;
        }
    }

    private void advanceToNextQuestion() {

        // move to next question
        currentQuestionIdx++;
        questionsAnsweredCount = 0;

        Question q = activeQuiz.getQuestionAt(currentQuestionIdx);

        // send question and options to all clients
        broadcast("QUESTION|" + currentQuestionIdx + "|" + q.getQuestionText() + "|" +
                q.getOption(0) + "|" + q.getOption(1) + "|" + q.getOption(2) + "|" + q.getOption(3));
    }

    private void evaluateRoundAnswers() {

        // get correct answer
        Question q = activeQuiz.getQuestionAt(currentQuestionIdx);
        int correctIdx = q.getCorrectOptionIndex();
        String correctText = q.getOption(correctIdx);

        // update scores
        for (Player p : playerRoster.values()) {
            if (p.isHost()) continue;

            boolean isCorrect = (p.getSelectedAnswer() == correctIdx);
            p.resolveAnswer(isCorrect, p.getAnswerTimeLeft());

            // clear answer for next round
            p.resetAnswer();
        }

        // build score update packet
        StringBuilder sb = new StringBuilder("REVEAL|").append(correctText).append("|");

        for (Map.Entry<String, Player> entry : playerRoster.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Host")) continue;

            sb.append(entry.getKey()).append(":")
              .append(entry.getValue().getScore()).append(",");
        }

        broadcast(sb.toString());
    }

    private void broadcastBetRoundPhase() {

        // send betting screen and scores
        StringBuilder sb = new StringBuilder("BET_ROUND|")
                .append(currentQuestionIdx + 1).append("|");

        for (Map.Entry<String, Player> entry : playerRoster.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Host")) continue;

            sb.append(entry.getKey()).append(":")
              .append(entry.getValue().getScore()).append(",");
        }

        broadcast(sb.toString());
    }

    private void broadcastFinalLeaderboard() {

        // send final game results
        StringBuilder sb = new StringBuilder("GAME_OVER|");

        for (Map.Entry<String, Player> entry : playerRoster.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Host")) continue;

            sb.append(entry.getKey()).append(":")
              .append(entry.getValue().getScore()).append(",");
        }

        broadcast(sb.toString());
    }

    private void broadcastPlayerRoster() {

        // send updated player list
        StringBuilder sb = new StringBuilder("PLAYER_LIST|");

        for (String name : playerRoster.keySet()) {
            sb.append(name).append(",");
        }

        broadcast(sb.toString());
    }

    public void broadcast(String message) {

        // send message to every connected client
        for (ClientHandler client : clients) {
            client.send(message);
        }
    }

    public synchronized void removeClient(ClientHandler client) {

        // remove disconnected client
        clients.remove(client);
    }

    public void stopServer() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {}
    }
}