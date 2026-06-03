package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * PlayerWaitingLobbyPanel: Shown to a player after joining, while waiting for host to start.
 * setMyName() and setPlayerList() are called from MainWindow via network messages.
 */
public class PlayerWaitingLobbyPanel extends JPanel {
    private final JLabel playerNameLabel;
    private final JPanel playerListPanel;
    private final JLabel countLabel;

    public PlayerWaitingLobbyPanel(MainWindow window) {
        setLayout(new BorderLayout());
        setBackground(new Color(24, 28, 48));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(24, 28, 48));
        center.setBorder(new EmptyBorder(50, 50, 50, 50));

        JLabel title = new JLabel("QuizRush");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 38));
        title.setForeground(Color.WHITE);

        JLabel waitingLabel = new JLabel("Waiting for host to start...");
        waitingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        waitingLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        waitingLabel.setForeground(new Color(180, 180, 200));

        JLabel connectedLabel = new JLabel("Connected as");
        connectedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectedLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        connectedLabel.setForeground(new Color(160, 160, 180));

        playerNameLabel = new JLabel("...");
        playerNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 30));
        playerNameLabel.setForeground(new Color(255, 215, 0));

        JLabel playersTitle = new JLabel("Players in Lobby");
        playersTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        playersTitle.setFont(new Font("Arial", Font.BOLD, 16));
        playersTitle.setForeground(Color.WHITE);

        countLabel = new JLabel("0 player(s)");
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        countLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        countLabel.setForeground(new Color(150, 200, 150));

        playerListPanel = new JPanel();
        playerListPanel.setLayout(new BoxLayout(playerListPanel, BoxLayout.Y_AXIS));
        playerListPanel.setBackground(new Color(24, 28, 48));
        playerListPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(title);
        center.add(Box.createRigidArea(new Dimension(0, 16)));
        center.add(waitingLabel);
        center.add(Box.createRigidArea(new Dimension(0, 30)));
        center.add(connectedLabel);
        center.add(Box.createRigidArea(new Dimension(0, 6)));
        center.add(playerNameLabel);
        center.add(Box.createRigidArea(new Dimension(0, 30)));
        center.add(playersTitle);
        center.add(Box.createRigidArea(new Dimension(0, 6)));
        center.add(countLabel);
        center.add(Box.createRigidArea(new Dimension(0, 12)));
        center.add(playerListPanel);

        add(center, BorderLayout.CENTER);
    }

    /** Called when the player's name is known after joining. */
    public void setMyName(String name) {
        SwingUtilities.invokeLater(() -> playerNameLabel.setText(name));
    }

    /** Called from MainWindow when PLAYER_LIST message arrives. */
    public void setPlayerList(String[] names) {
        SwingUtilities.invokeLater(() -> {
            playerListPanel.removeAll();
            for (String name : names) {
                JLabel lbl = new JLabel("- " + name);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Arial", Font.PLAIN, 16));
                lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                playerListPanel.add(lbl);
                playerListPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            }
            countLabel.setText(names.length + " player(s)");
            playerListPanel.revalidate();
            playerListPanel.repaint();
        });
    }
}
