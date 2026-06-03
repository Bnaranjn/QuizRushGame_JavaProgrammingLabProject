package network;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * QuizServer: Accepts incoming client connections and handles broadcasting.
 * Fixed: clients stored in a synchronized list, sender excluded from echo,
 * player JOIN messages parsed and re-broadcast as PLAYER_LIST updates.
 */
public class QuizServer {
    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private final List<String> playerNames = Collections.synchronizedList(new ArrayList<>());

    //added by nj - used for leaderboard broadcast
    private final Map<String, Integer> playerScores = Collections.synchronizedMap(new LinkedHashMap<>());
    //server startup
    
    public void startServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHandler client = new ClientHandler(socket, this);
                    clients.add(client);
                    client.start();
                    System.out.println("New client connected. Total: " + clients.size());
                } catch (Exception e) {
                    if (!serverSocket.isClosed()) e.printStackTrace();
                    break;
                }
            }
        }, "ServerAcceptThread").start();
    }

    /**
     * Called by ClientHandler when a message arrives from a client.
     * Parses protocol messages and decides what to broadcast.
     */
    public void handleMessage(String message, ClientHandler sender) {
        System.out.println("Server received: " + message);

        if (message.startsWith("JOIN|")) {
        	//modified by nj = excluding the host 
            String name = message.substring(5).trim();
            
            if (!name.contentEquals("Host")&&!playerNames.contains(name)) {
                playerNames.add(name);
                playerScores.put(name, 0);//initalizing the score
            }
            // Broadcast updated player list to everyone
            broadcastPlayerList();

            //else if function added here for score update
        } else if (message.startsWith("SCORE_UPDATE|")) {
            // Format: SCORE_UPDATE|<name>|<score>
            String[] parts = message.split("\\|");
            if (parts.length == 3) {
                try {
                    String name  = parts[1];
                    int    score = Integer.parseInt(parts[2]);
                    playerScores.put(name, score);   //server manages the score
                    System.out.println("Score recorded: " + name + " = " + score);
                } catch (NumberFormatException ignored) {}
            }
            // Also broadcast so every client's UI can show live score updates
            broadcast(message);
        } else {
            // All other messages (START_GAME, QUESTION|..., TIMER_SYNC|..., etc.)
            // broadcast to ALL clients including the host's own client if connected
            broadcast(message);
        }
    }

    //added by nj - leaderboard build
    public void broadcastLeaderboard() {
        StringBuilder sb = new StringBuilder();
        synchronized (playerScores) {
            playerScores.forEach((name, score) ->
                sb.append(name).append(":").append(score).append(","));
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 1);   // trim trailing comma
        broadcast("LEADERBOARD|" + sb);
        broadcast("GAME_OVER");
    }
    
    /** Sends the current player list to all clients. */
    private void broadcastPlayerList() {
        String joined = String.join(",", playerNames);
        broadcast("PLAYER_LIST|" + joined);
    }

    /** Sends a message to every connected client. */
    public void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler c : clients) {
                c.send(message);
            }
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client disconnected. Remaining: " + clients.size());
    }

    public void stopServer() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
