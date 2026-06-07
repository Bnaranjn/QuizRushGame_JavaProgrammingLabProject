package network;

import java.io.*;
import java.net.Socket;

public class QuizClient {
    
    // connection to the server
    private Socket socket;
    
    // streams used for sending and receiving data
    private DataInputStream in;
    private DataOutputStream out;
    
    // separate thread for reading incoming messages
    private Thread readThread;
    
    // notifies the gui when a message arrives
    private final MessageListener listener;

    public QuizClient(String host, int port, MessageListener listener) throws IOException {
        this.listener = listener;

        // connect to the server
        this.socket = new Socket(host, port);

        // setup communication streams
        this.in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));
        this.out = new DataOutputStream(
                new BufferedOutputStream(socket.getOutputStream()));

        // keeps listening for messages from server
        readThread = new Thread(() -> {
            while (!socket.isClosed()) {
                try {
                    String message = in.readUTF();

                    // pass received message to whoever is listening
                    listener.onMessageReceived(message);

                } catch (IOException e) {
                    // stop reading if connection is lost
                    break;
                }
            }
        });

        // daemon thread closes automatically with application
        readThread.setDaemon(true);
        readThread.start();
    }

    // sends a message to the server
    public void send(String message) {
        try {
            out.writeUTF(message);
            out.flush(); // send it immediately
        } catch (IOException e) {
            System.err.println(
                    "Failed to write packet downstream: " + e.getMessage());
        }
    }

    // closes the client connection
    public void disconnect() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException ignored) {
            // ignore close errors
        }
    }
}