package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// This panel is shown to the player after they submit an answer or bet
// It just displays a holding message while waiting for others to finish
public class PlayerWaitingPanel extends JPanel {

    // Updated dynamically depending on which phase the player is waiting in
    private final JLabel statusLabel;

    public PlayerWaitingPanel(MainWindow window) {
        setLayout(new BorderLayout());
        setBackground(new Color(24, 28, 48)); // Dark navy background
        setBorder(new EmptyBorder(40, 40, 40, 40)); // Padding on all sides

        // Title at the top confirming the player's action was recorded
        JLabel titleLabel = new JLabel("RESPONSE LOCKED IN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(46, 204, 113)); // Green to signal success
        add(titleLabel, BorderLayout.NORTH);

        // Default waiting message shown in the center of the screen
        statusLabel = new JLabel("<html><center>Waiting for other players to finish...<br><font color='#95a5a6'>Please hold. The round results scoreboard will appear shortly.</font></center></html>", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        statusLabel.setForeground(Color.WHITE);
        add(statusLabel, BorderLayout.CENTER);
    }

    // Called from MainWindow to update the message depending on the current phase
    // Runs on the EDT since it touches Swing components
    public void setMessage(String heading, String subtext) {
        SwingUtilities.invokeLater(() -> 
            statusLabel.setText("<html><center>" + heading + "<br><font color='#95a5a6'>" + subtext + "</font></center></html>")
        );
    }
}