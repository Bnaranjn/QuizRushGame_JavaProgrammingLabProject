package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HostWaitingPanel extends JPanel {

    // Label used to display the current waiting status to the host
    private final JLabel statusLabel;

    public HostWaitingPanel(MainWindow window) {
        setLayout(new BorderLayout());
        setBackground(new Color(24, 28, 48));
        setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header shown at the top of the waiting screen
        JLabel titleLabel = new JLabel("HOST CONTROL RADAR", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(241, 196, 15));
        add(titleLabel, BorderLayout.NORTH);

        // Default message shown while players are submitting answers
        statusLabel = new JLabel(
            "<html><center>Players are currently submitting choices.<br>" +
            "<font color='#95a5a6'>The progression sequence will resume automatically once all players submit responses.</font>" +
            "</center></html>",
            SwingConstants.CENTER
        );

        statusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        statusLabel.setForeground(Color.WHITE);

        add(statusLabel, BorderLayout.CENTER);
    }

    // Updates the host-facing status message safely on the Swing UI thread
    public void setMessage(String heading, String subtext) {
        SwingUtilities.invokeLater(() ->
            statusLabel.setText(
                "<html><center>" + heading +
                "<br><font color='#95a5a6'>" + subtext +
                "</font></center></html>"
            )
        );
    }
}