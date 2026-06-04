package network;

import java.io.*;
import java.net.Socket;

public class QuizClient {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Thread readThread;
    private final MessageListener listener;

    public QuizClient(String host, int port, MessageListener listener) throws IOException {
        this.listener = listener;
        this.socket = new Socket(host, port);
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        readThread = new Thread(() -> {
            while (!socket.isClosed()) {
                try {
                    String message = in.readUTF();
                    listener.onMessageReceived(message);
                } catch (IOException e) {
                    break;
                }
            }
        });
        readThread.setDaemon(true);
        readThread.start();
    }

    public void send(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            System.err.println("Failed to write packet downstream: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}