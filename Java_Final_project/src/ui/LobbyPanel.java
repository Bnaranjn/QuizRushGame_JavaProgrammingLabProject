package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class LobbyPanel extends JPanel {
    private final MainWindow window;
    private final DefaultListModel<String> listModel;
    private final JButton startBtn;
    private final JLabel lobbyCodeLabel;
    private final JLabel countLabel; // CHANGED: added player count label like the first panel

    // CHANGED: colour palette brought in from the first panel
    private static final Color BG         = new Color(24, 28, 48);
    private static final Color TEXT_COLOR = Color.WHITE;

    public LobbyPanel(MainWindow window) {
        this.window = window;
        this.listModel = new DefaultListModel<>();

        // CHANGED: switched from BorderLayout to BoxLayout Y_AXIS to match first panel's card style
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG);
        setBorder(new EmptyBorder(50, 60, 50, 60)); // CHANGED: padding matches first panel

        // CHANGED: title style matches first panel ("QuizRush - Lobby", bold 32pt)
        JLabel titleLabel = new JLabel("QuizRush - Lobby");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_COLOR);

        // CHANGED: "Room PIN" caption label above the port number, like first panel's pinTitle
        JLabel portCaption = new JLabel("Room Port");
        portCaption.setAlignmentX(Component.CENTER_ALIGNMENT);
        portCaption.setFont(new Font("Arial", Font.BOLD, 15));
        portCaption.setForeground(new Color(180, 180, 200));

        // CHANGED: port number now large yellow 44pt like the first panel's pinLabel
        lobbyCodeLabel = new JLabel("----");
        lobbyCodeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        lobbyCodeLabel.setFont(new Font("Arial", Font.BOLD, 44));
        lobbyCodeLabel.setForeground(new Color(255, 215, 0));

        // CHANGED: player count label copied from first panel
        countLabel = new JLabel("0 player(s) joined");
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        countLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        countLabel.setForeground(new Color(150, 200, 150));

        // CHANGED: player list area is now a plain JPanel with chip labels (like first panel)
        // still backed by DefaultListModel so updatePlayerList() works without changes
        JPanel playerListPanel = new JPanel();
        playerListPanel.setLayout(new BoxLayout(playerListPanel, BoxLayout.Y_AXIS));
        playerListPanel.setBackground(BG);
        playerListPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // keep the JList hidden behind the scenes so updatePlayerList() can still populate it,
        // but render the chip panel visually — see updatePlayerList() below
        JList<String> hiddenList = new JList<>(listModel); // logic anchor, not displayed

        JScrollPane scroll = new JScrollPane(playerListPanel);
        scroll.setBackground(BG);
        scroll.setBorder(BorderFactory.createEmptyBorder()); // CHANGED: no border like first panel
        scroll.setPreferredSize(new Dimension(360, 160));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        scroll.getViewport().setBackground(BG);

        // CHANGED: Start Quiz button style matches first panel (blue, centered, EmptyBorder padding)
        startBtn = new JButton("Start Quiz");
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.setFont(new Font("Arial", Font.BOLD, 18));
        startBtn.setBackground(new Color(90, 120, 255)); // CHANGED: blue like first panel, was green
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setBorder(new EmptyBorder(12, 40, 12, 40)); // CHANGED: pill padding like first panel
        startBtn.setMaximumSize(new Dimension(260, 52));

        startBtn.addActionListener(e -> {
            String fileName = JOptionPane.showInputDialog(
                window,
                "Enter the name of your quiz source file (e.g., quiz.txt):",
                "Load Quiz Configuration",
                JOptionPane.QUESTION_MESSAGE
            );
            if (fileName != null && !fileName.trim().isEmpty()) {
                window.hostLoadAndStartQuiz(fileName.trim());
            }
        });

        // CHANGED: assemble with Box spacing like first panel
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(portCaption);
        add(Box.createRigidArea(new Dimension(0, 4)));
        add(lobbyCodeLabel);
        add(Box.createRigidArea(new Dimension(0, 6)));
        add(countLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(scroll);
        add(Box.createRigidArea(new Dimension(0, 24)));
        add(startBtn);

        // store chip panel ref for updatePlayerList
        putClientProperty("chipPanel", playerListPanel);
    }

    public void setRoomPortDisplay(int activePort) {
        // CHANGED: show just the number (caption is a separate label above)
        SwingUtilities.invokeLater(() -> lobbyCodeLabel.setText(String.valueOf(activePort)));
    }

    public void updatePlayerList(List<String> players) {
        SwingUtilities.invokeLater(() -> {
            // update the hidden model (keeps API intact)
            listModel.clear();
            for (String p : players) listModel.addElement(p);

            // CHANGED: also rebuild chip labels in the visual panel, like the first panel does
            JPanel chipPanel = (JPanel) getClientProperty("chipPanel");
            chipPanel.removeAll();
            Color[] chipColors = {
                new Color(100, 220, 150),
                new Color(100, 180, 255),
                new Color(255, 180, 100),
                new Color(220, 120, 200),
                new Color(120, 220, 220)
            };
            int ci = 0;
            for (String name : players) {
                JLabel chip = new JLabel(">> " + name);
                chip.setForeground(chipColors[ci % chipColors.length]);
                chip.setFont(new Font("Arial", Font.BOLD, 16));
                chip.setAlignmentX(Component.CENTER_ALIGNMENT);
                chipPanel.add(chip);
                chipPanel.add(Box.createRigidArea(new Dimension(0, 6)));
                ci++;
            }
            // CHANGED: update count label like first panel
            countLabel.setText(players.size() + " player(s) joined");
            chipPanel.revalidate();
            chipPanel.repaint();
        });
    }

    public void setHostControls(boolean isHost) {
        SwingUtilities.invokeLater(() -> startBtn.setVisible(isHost));
    }
}