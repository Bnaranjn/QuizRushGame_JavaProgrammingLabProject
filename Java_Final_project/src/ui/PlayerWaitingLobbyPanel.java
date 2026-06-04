package ui;

import javax.swing.*;
import java.awt.*;

public class PlayerWaitingLobbyPanel extends JPanel {
    public PlayerWaitingLobbyPanel(MainWindow window) {
        setLayout(new BorderLayout());
        setBackground(new Color(24, 28, 48));

        JLabel holdingText = new JLabel("<html><center>Synchronized with Session Lobby!<br><font color='#95a5a6'>Waiting for host to load game questions...</font></center></html>", SwingConstants.CENTER);
        holdingText.setFont(new Font("Arial", Font.BOLD, 20));
        holdingText.setForeground(Color.WHITE);
        
        add(holdingText, BorderLayout.CENTER);
    }
}