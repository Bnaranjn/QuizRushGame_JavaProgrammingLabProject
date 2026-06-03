package network;

import java.io.*;
import java.net.*;

/**
 * QuizClient: Connects to the QuizServer and sends/receives messages.
 */
public class QuizClient {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final MessageListener listener;

    public QuizClient(String ip, int port, MessageListener listener) throws IOException {
        this.listener = listener;
        socket = new Socket(ip, port);
        in  = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        startListening();
    }

    private void startListening() {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    String message = in.readUTF();
                    listener.onMessageReceived(message);
                }
            } catch (Exception e) {
                System.out.println("Disconnected from server.");
            }
        }, "ClientListenThread");
        t.setDaemon(true);
        t.start();
    }

    public void send(String message) throws IOException {
        out.writeUTF(message);
        out.flush();
    }

    public void close() {
        try { socket.close(); } catch (IOException ignored) {}
    }
}
