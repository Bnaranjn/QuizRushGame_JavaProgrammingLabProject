package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PlayerWaitingLobbyPanel extends JPanel {

    private JPanel playerListPanel;

    public PlayerWaitingLobbyPanel(String playerName) {

        setLayout(new BorderLayout());
        setBackground(new Color(24, 28, 48));

        // ================= CENTER PANEL =================

        JPanel centerPanel = new JPanel();

        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        centerPanel.setBackground(new Color(24, 28, 48));

        centerPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        // TITLE

        JLabel title = new JLabel("QuizRush");

        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        title.setFont(new Font("Arial", Font.BOLD, 38));

        title.setForeground(Color.WHITE);

        // WAITING MESSAGE

        JLabel waitingLabel = new JLabel("Waiting for host to start...");

        waitingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        waitingLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        waitingLabel.setForeground(new Color(200, 200, 200));

        // PLAYER NAME

        JLabel connectedLabel = new JLabel("Connected as");

        connectedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        connectedLabel.setForeground(new Color(180, 180, 180));

        connectedLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        JLabel playerNameLabel = new JLabel(playerName);

        playerNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        playerNameLabel.setForeground(new Color(255, 215, 0));

        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 32));

        // PLAYERS TITLE

        JLabel playersTitle = new JLabel("Players Joined");

        playersTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        playersTitle.setForeground(Color.WHITE);

        playersTitle.setFont(new Font("Arial", Font.BOLD, 20));

        // PLAYER LIST PANEL

        playerListPanel = new JPanel();

        playerListPanel.setLayout(new BoxLayout(playerListPanel, BoxLayout.Y_AXIS));

        playerListPanel.setBackground(new Color(24, 28, 48));

        playerListPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // SAMPLE PLAYERS

        addPlayer("Alice");
        addPlayer("Bob");
        addPlayer(playerName);

        // ADD COMPONENTS

        centerPanel.add(title);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        centerPanel.add(waitingLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        centerPanel.add(connectedLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        centerPanel.add(playerNameLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        centerPanel.add(playersTitle);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        centerPanel.add(playerListPanel);

        add(centerPanel, BorderLayout.CENTER);
    }

    // reusable player display

    public void addPlayer(String playerName) {

        JLabel playerLabel = new JLabel("• " + playerName);

        playerLabel.setForeground(Color.WHITE);

        playerLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        playerListPanel.add(playerLabel);

        playerListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
}