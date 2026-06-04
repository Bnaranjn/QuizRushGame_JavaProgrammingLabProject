package network;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final QuizServer server;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler(Socket socket, QuizServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void send(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            server.removeClient(this);
        }
    }

    @Override
    public void run() {
        try {
            this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            while (!socket.isClosed()) {
                String message = in.readUTF();
                server.handleMessage(message, this);
            }
        } catch (IOException e) {
            // Catches natural window socket terminations gracefully
        } finally {
            server.removeClient(this);
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}