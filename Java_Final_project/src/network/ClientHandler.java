package network;

import java.io.*;
import java.net.Socket;

/**
 * handles one connected client
 * runs on its own thread
 */
public class ClientHandler extends Thread {
	// socket for this specific client
    private final Socket socket;
    // reference to the main server
    private final QuizServer server;
    private DataInputStream in;
    private DataOutputStream out;

    
    public ClientHandler(Socket socket, QuizServer server) {
        this.socket = socket;
        this.server = server;
    }
    // sends a message back to the client

    public void send(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
        	// if sending fails remove client from server list
            server.removeClient(this);
        }
    }

    @Override
    public void run() {
        try {
        	// create input/output streams for communication
            this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            // keep listening while client is connected
            while (!socket.isClosed()) {
                String message = in.readUTF();
                // pass received message to server for processing
                server.handleMessage(message, this);
            }
        } catch (IOException e) {
            //catches natural window socket terminations 
        } finally {
            server.removeClient(this);
            // usually happens when client closes the window
            // no need to print error here
            try {
                socket.close();
            } catch (IOException ignored) {
            	// ignore close errors
            }
        }
    }
}