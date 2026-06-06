package network;

import QuizGame.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class QuizServer {
    private ServerSocket serverSocket;
    private Thread acceptThread;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final Map<String, Player> playerRoster = new ConcurrentHashMap<>();
    
    private Quiz activeQuiz;
    private int currentQuestionIdx = -1;
    private int questionsAnsweredCount = 0;
    private int betsPlacedCount = 0;

    public void startServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        acceptThread = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(socket, this);
                    clients.add(handler);
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
        for (String name : playerRoster.keySet()) {
            if (!name.equalsIgnoreCase("Host")) {
                count++;
            }
        }
        return count;
    }

    public synchronized void handleMessage(String message, ClientHandler sender) {
        String[] tokens = message.split("\\|");
        String command = tokens[0];

        switch (command) {
            case "JOIN":
                String joinName = tokens[1];
                if (!playerRoster.containsKey(joinName)) {
                    Player newPlayer = new Player(joinName);
                    if (joinName.equalsIgnoreCase("Host")) {
                        newPlayer.setHost(true);
                    }
                    playerRoster.put(joinName, newPlayer);
                }
                broadcastPlayerRoster();
                break;

            case "LEAVE":
                String leaveName = tokens[1];
                playerRoster.remove(leaveName);
                broadcastPlayerRoster();
                
                // Safety: Re-verify submission limits in case a player left mid-round
                int remainingPlayers = getActivePlayerCount();
                if (remainingPlayers > 0) {
                    if (questionsAnsweredCount >= remainingPlayers) {
                        evaluateRoundAnswers();
                    }
                    if (betsPlacedCount >= remainingPlayers) {
                        betsPlacedCount = 0;
                        advanceToNextQuestion();
                    }
                }
                break;

            case "START_GAME":
                String targetFile = tokens.length > 1 ? tokens[1] : "quiz.txt";
                activeQuiz = new Quiz();
                activeQuiz.loadQuizFromFile(targetFile);
                
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

                if (answeringPlayer.equalsIgnoreCase("Host")) return;

                Player pAnswer = playerRoster.get(answeringPlayer);
                if (pAnswer != null) {
                    pAnswer.setSelectedAnswer(answerIndex);
                    pAnswer.setAnswerTimeLeft(elapsedBonus);
                }

                questionsAnsweredCount++;
                if (questionsAnsweredCount >= getActivePlayerCount()) {
                    evaluateRoundAnswers();
                }
                break;

            case "BET":
                String bettingPlayer = tokens[1];
                int wagerAmount = Integer.parseInt(tokens[2]);
                int multValue = Integer.parseInt(tokens[3]);

                if (bettingPlayer.equalsIgnoreCase("Host")) return;

                Player pBet = playerRoster.get(bettingPlayer);
                if (pBet != null) {
                    pBet.placeBet(wagerAmount, multValue);
                }

                betsPlacedCount++;
                if (betsPlacedCount >= getActivePlayerCount()) {
                    betsPlacedCount = 0;
                    advanceToNextQuestion();
                }
                break;

            case "REQUEST_NEXT_PHASE":
                int totalCount = activeQuiz.getTotalQuestions();
                GameSession.NextEvent event = GameSession.getNextEvent(currentQuestionIdx, totalCount);

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
        currentQuestionIdx++;
        questionsAnsweredCount = 0;
        Question q = activeQuiz.getQuestionAt(currentQuestionIdx);
        
        broadcast("QUESTION|" + currentQuestionIdx + "|" + q.getQuestionText() + "|" +
                q.getOption(0) + "|" + q.getOption(1) + "|" + q.getOption(2) + "|" + q.getOption(3));
    }

    private void evaluateRoundAnswers() {
        Question q = activeQuiz.getQuestionAt(currentQuestionIdx);
        int correctIdx = q.getCorrectOptionIndex();
        String correctText = q.getOption(correctIdx);

        for (Player p : playerRoster.values()) {
            if (p.isHost()) continue;
            
            boolean isCorrect = (p.getSelectedAnswer() == correctIdx);
            p.resolveAnswer(isCorrect, p.getAnswerTimeLeft());
            p.resetAnswer();
        }

        StringBuilder sb = new StringBuilder("REVEAL|").append(correctText).append("|");
        for (Map.Entry<String, Player> entry : playerRoster.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Host")) continue;
            sb.append(entry.getKey()).append(":").append(entry.getValue().getScore()).append(",");
        }
        broadcast(sb.toString());
    }

    private void broadcastBetRoundPhase() {
        StringBuilder sb = new StringBuilder("BET_ROUND|").append(currentQuestionIdx + 1).append("|");
        for (Map.Entry<String, Player> entry : playerRoster.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Host")) continue;
            sb.append(entry.getKey()).append(":").append(entry.getValue().getScore()).append(",");
        }
        broadcast(sb.toString());
    }

    private void broadcastFinalLeaderboard() {
        StringBuilder sb = new StringBuilder("GAME_OVER|");
        for (Map.Entry<String, Player> entry : playerRoster.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Host")) continue;
            sb.append(entry.getKey()).append(":").append(entry.getValue().getScore()).append(",");
        }
        broadcast(sb.toString());
    }

    private void broadcastPlayerRoster() {
        StringBuilder sb = new StringBuilder("PLAYER_LIST|");
        for (String name : playerRoster.keySet()) {
            sb.append(name).append(",");
        }
        broadcast(sb.toString());
    }

    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.send(message);
        }
    }

    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public void stopServer() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {}
    }
}