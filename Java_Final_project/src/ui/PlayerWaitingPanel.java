package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PlayerWaitingPanel extends JPanel {
    private final JLabel statusLabel;

    public PlayerWaitingPanel(MainWindow window) {
        setLayout(new BorderLayout());
        setBackground(new Color(24, 28, 48));
        setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("RESPONSE LOCKED IN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(46, 204, 113));
        add(titleLabel, BorderLayout.NORTH);

        statusLabel = new JLabel("<html><center>Waiting for other players to finish...<br><font color='#95a5a6'>Please hold. The round results scoreboard will appear shortly.</font></center></html>", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        statusLabel.setForeground(Color.WHITE);
        add(statusLabel, BorderLayout.CENTER);
    }

    public void setMessage(String heading, String subtext) {
        SwingUtilities.invokeLater(() -> 
            statusLabel.setText("<html><center>" + heading + "<br><font color='#95a5a6'>" + subtext + "</font></center></html>")
        );
    }
}