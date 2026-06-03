package network;

import java.io.*;
import java.net.*;

/**
 * ClientHandler: One thread per connected client.
 * Fixed: passes messages to server.handleMessage() so JOIN can be parsed
 * and messages are not echoed blindly.
 */
public class ClientHandler extends Thread {
    private final Socket socket;
    private final QuizServer server;
    private final DataInputStream in;
    private final DataOutputStream out;

    public ClientHandler(Socket socket, QuizServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        in  = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = in.readUTF();
                server.handleMessage(message, this);
            }
        } catch (Exception e) {
            server.removeClient(this);
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    public void send(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (Exception e) {
            server.removeClient(this);
        }
    }
}
