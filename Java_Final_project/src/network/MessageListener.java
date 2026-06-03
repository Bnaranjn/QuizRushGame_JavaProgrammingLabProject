package network;

/**
 * MessageListener: Callback interface for receiving messages from the server.
 */
public interface MessageListener {
    void onMessageReceived(String message);
}
