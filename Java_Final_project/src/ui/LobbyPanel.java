package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * LobbyPanel: Host sees this after opening the lobby.
 * Players are added dynamically via setPlayerList() driven by PLAYER_LIST network messages.
 */
public class LobbyPanel extends JPanel {
    private final MainWindow window;
    private final JPanel playerListPanel;
    private final JLabel countLabel;

    public LobbyPanel(MainWindow window, int pin) {
        this.window = window;
        setLayout(new BorderLayout());
        setBackground(new Color(24, 28, 48));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(24, 28, 48));
        center.setBorder(new EmptyBorder(50, 60, 50, 60));

        JLabel title = new JLabel("QuizRush - Lobby");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        JLabel pinTitle = new JLabel("Room PIN");
        pinTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        pinTitle.setFont(new Font("Arial", Font.BOLD, 15));
        pinTitle.setForeground(new Color(180, 180, 200));

        JLabel pinLabel = new JLabel(String.valueOf(pin));
        pinLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        pinLabel.setFont(new Font("Arial", Font.BOLD, 44));
        pinLabel.setForeground(new Color(255, 215, 0));

        countLabel = new JLabel("0 player(s) joined");
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        countLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        countLabel.setForeground(new Color(150, 200, 150));

        // Player chip area
        playerListPanel = new JPanel();
        playerListPanel.setLayout(new BoxLayout(playerListPanel, BoxLayout.Y_AXIS));
        playerListPanel.setBackground(new Color(24, 28, 48));
        playerListPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JScrollPane scroll = new JScrollPane(playerListPanel);
        scroll.setBackground(new Color(24, 28, 48));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(360, 160));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        scroll.getViewport().setBackground(new Color(24, 28, 48));

        JButton startBtn = new JButton("Start Quiz");
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.setFont(new Font("Arial", Font.BOLD, 18));
        startBtn.setBackground(new Color(90, 120, 255));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setBorder(new EmptyBorder(12, 40, 12, 40));
        startBtn.setMaximumSize(new Dimension(260, 52));
        startBtn.addActionListener(e -> window.startQuizForEveryone());

        center.add(title);
        center.add(Box.createRigidArea(new Dimension(0, 20)));
        center.add(pinTitle);
        center.add(Box.createRigidArea(new Dimension(0, 4)));
        center.add(pinLabel);
        center.add(Box.createRigidArea(new Dimension(0, 6)));
        center.add(countLabel);
        center.add(Box.createRigidArea(new Dimension(0, 20)));
        center.add(scroll);
        center.add(Box.createRigidArea(new Dimension(0, 24)));
        center.add(startBtn);

        add(center, BorderLayout.CENTER);
    }

    /** Called from MainWindow when PLAYER_LIST message arrives. */
    public void setPlayerList(String[] names) {
        SwingUtilities.invokeLater(() -> {
            playerListPanel.removeAll();
            Color[] chipColors = {
                new Color(100, 220, 150),
                new Color(100, 180, 255),
                new Color(255, 180, 100),
                new Color(220, 120, 200),
                new Color(120, 220, 220)
            };
            int ci = 0;
            for (String name : names) {
                JLabel chip = new JLabel(">> " + name);
                chip.setForeground(chipColors[ci % chipColors.length]);
                chip.setFont(new Font("Arial", Font.BOLD, 16));
                chip.setAlignmentX(Component.CENTER_ALIGNMENT);
                playerListPanel.add(chip);
                playerListPanel.add(Box.createRigidArea(new Dimension(0, 6)));
                ci++;
            }
            countLabel.setText(names.length + " player(s) joined");
            playerListPanel.revalidate();
            playerListPanel.repaint();
        });
    }
}
