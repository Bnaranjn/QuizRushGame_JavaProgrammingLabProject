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
            String name = message.substring(5).trim();
            if (!playerNames.contains(name)) {
                playerNames.add(name);
            }
            // Broadcast updated player list to everyone
            broadcastPlayerList();

        } else {
            // All other messages (START_GAME, QUESTION|..., TIMER_SYNC|..., etc.)
            // broadcast to ALL clients including the host's own client if connected
            broadcast(message);
        }
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
