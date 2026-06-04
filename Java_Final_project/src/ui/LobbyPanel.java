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

    private static final Color BG = new Color(24, 28, 48);
    private static final Color TEXT_COLOR = Color.WHITE;

    public LobbyPanel(MainWindow window) {
        this.window = window;
        this.listModel = new DefaultListModel<>();

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(BG);

        JLabel titleLabel = new JLabel("GAME LOBBY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        headerPanel.add(titleLabel);

        // Left with dynamic placeholder token text
        lobbyCodeLabel = new JLabel("Lobby Room Port: Initializing...", SwingConstants.CENTER);
        lobbyCodeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        lobbyCodeLabel.setForeground(new Color(150, 160, 200));
        headerPanel.add(lobbyCodeLabel);

        add(headerPanel, BorderLayout.NORTH);

        JList<String> playerList = new JList<>(listModel);
        playerList.setBackground(new Color(36, 42, 73));
        playerList.setForeground(TEXT_COLOR);
        playerList.setFont(new Font("Arial", Font.PLAIN, 18));
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(playerList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 60, 100)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(BG);
        footerPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        startBtn = new JButton("Start Quiz");
        startBtn.setFont(new Font("Arial", Font.BOLD, 18));
        startBtn.setBackground(new Color(40, 167, 69));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setPreferredSize(new Dimension(0, 50));
        
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

        footerPanel.add(startBtn, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    /** Updates the descriptive UI header to visually reflect the dynamic port choice */
    public void setRoomPortDisplay(int activePort) {
        SwingUtilities.invokeLater(() -> lobbyCodeLabel.setText("Lobby Room Port: " + activePort));
    }

    public void updatePlayerList(List<String> players) {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            for (String p : players) {
                listModel.addElement(p);
            }
        });
    }

    public void setHostControls(boolean isHost) {
        SwingUtilities.invokeLater(() -> startBtn.setVisible(isHost));
    }
}