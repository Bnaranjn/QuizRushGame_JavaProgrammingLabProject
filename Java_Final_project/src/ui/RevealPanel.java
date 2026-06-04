package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class RevealPanel extends JPanel {
    private final MainWindow window;
    private final JLabel correctOptionLabel;
    private final JTextArea scoreboardArea;
    private final JButton nextBtn;

    private static final Color BG = new Color(24, 28, 48);
    private static final Color TEXT_COLOR = Color.WHITE;

    public RevealPanel(MainWindow window) {
        this.window = window;
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Section Title View
        JLabel titleLabel = new JLabel("ROUND RESULTS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(new Color(46, 204, 113));
        add(titleLabel, BorderLayout.NORTH);

        // Central Layout for displaying Metrics & Leaderboards
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(BG);

        correctOptionLabel = new JLabel("Correct Option Index: -", SwingConstants.CENTER);
        correctOptionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        correctOptionLabel.setForeground(new Color(241, 196, 15));
        centerPanel.add(correctOptionLabel, BorderLayout.NORTH);

        scoreboardArea = new JTextArea();
        scoreboardArea.setEditable(false);
        scoreboardArea.setBackground(new Color(36, 42, 73));
        scoreboardArea.setForeground(TEXT_COLOR);
        scoreboardArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        scoreboardArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scroll = new JScrollPane(scoreboardArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(50, 60, 100)));
        centerPanel.add(scroll, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Admin Flow Progression Controller
        nextBtn = new JButton("Advance Phase");
        nextBtn.setFont(new Font("Arial", Font.BOLD, 18));
        nextBtn.setBackground(new Color(52, 152, 219));
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFocusPainted(false);
        
        // Tells server to evaluate what screen state follows next (Question vs Bet vs Podium)
        nextBtn.addActionListener(e -> window.hostRequestsNextPhase());

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(BG);
        footerPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        footerPanel.add(nextBtn, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    /** Called from MainWindow when processing the REVEAL command packet string */
    public void showResults(int correctIndex, Map<String, Integer> currentStandings, String myName, boolean isHost) {
        SwingUtilities.invokeLater(() -> {
            // Only reveal the Next navigation control to the host administrator
            nextBtn.setVisible(isHost);
            
            correctOptionLabel.setText("Correct Choice Option Index: " + correctIndex);
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-20s | %-10s\n", "PLAYER NAME", "TOTAL SCORE"));
            sb.append("-------------------------------------------\n");
            
            for (Map.Entry<String, Integer> entry : currentStandings.entrySet()) {
                String marker = entry.getKey().equals(myName) ? " (You)" : "";
                sb.append(String.format("%-20s | %-10d pts\n", entry.getKey() + marker, entry.getValue()));
            }
            
            scoreboardArea.setText(sb.toString());
        });
    }
}