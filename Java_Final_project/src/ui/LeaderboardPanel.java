package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * LeaderboardPanel: Final screen showing player rankings.
 * Fixed: showLeaderboard() populates from real game scores (via LEADERBOARD network message),
 * instead of hardcoded sample data.
 */
public class LeaderboardPanel extends JPanel {
    private final MainWindow window;
    private final JPanel listPanel;
    private final JLabel titleLabel;

    public LeaderboardPanel(MainWindow window) {
        this.window = window;
        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 30));

        // Title
        titleLabel = new JLabel("Final Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(28, 10, 16, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Scrollable list
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(18, 18, 30));
        listPanel.setBorder(new EmptyBorder(10, 30, 10, 30));

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBackground(new Color(18, 18, 30));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(18, 18, 30));
        add(scroll, BorderLayout.CENTER);

        // Play Again / Close button
        JButton closeBtn = new JButton("Close Game");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 18));
        closeBtn.setBackground(new Color(90, 120, 255));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(new EmptyBorder(14, 30, 14, 30));
        closeBtn.addActionListener(e -> System.exit(0));

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(18, 18, 30));
        bottom.setBorder(new EmptyBorder(16, 20, 24, 20));
        bottom.add(closeBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    /**
     * Populates the leaderboard from real game data.
     * @param scores  map of name → score (should be in insertion order, already sorted by caller)
     * @param myName  this player's name, highlighted differently
     */
    public void showLeaderboard(Map<String, Integer> scores, String myName) {
        SwingUtilities.invokeLater(() -> {
            listPanel.removeAll();

            // Sort descending by score
            List<Map.Entry<String, Integer>> sorted = new ArrayList<>(scores.entrySet());
            sorted.sort((a, b) -> b.getValue() - a.getValue());

            String[] medals = {"1st", "2nd", "3rd"};
            int rank = 1;
            for (Map.Entry<String, Integer> entry : sorted) {
                String  name  = entry.getKey();
                int     score = entry.getValue();
                boolean isMe  = name.equals(myName);
                addRow(rank, name, score, isMe, medals);
                rank++;
            }
            listPanel.revalidate();
            listPanel.repaint();
        });
    }

    private void addRow(int rank, String name, int score, boolean isMe, String[] medals) {
        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));

        Color rowBg = isMe
            ? new Color(40, 60, 100)    // highlight "you"
            : new Color(30, 30, 50);
        row.setBackground(rowBg);
        row.setBorder(new EmptyBorder(14, 18, 14, 18));

        String rankText = (rank <= 3) ? medals[rank - 1] : "#" + rank;
        JLabel rankLbl = new JLabel(rankText);
        rankLbl.setFont(new Font("Arial", Font.BOLD, 22));
        rankLbl.setForeground(rank == 1 ? new Color(255, 215, 0) : Color.WHITE);
        rankLbl.setPreferredSize(new Dimension(50, 30));

        JLabel nameLbl = new JLabel(name + (isMe ? " (you)" : ""));
        nameLbl.setForeground(Color.WHITE);
        nameLbl.setFont(new Font("Arial", Font.BOLD, isMe ? 20 : 18));

        JLabel scoreLbl = new JLabel(score + " pts");
        scoreLbl.setForeground(new Color(255, 215, 0));
        scoreLbl.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(rankLbl,  BorderLayout.WEST);
        row.add(nameLbl,  BorderLayout.CENTER);
        row.add(scoreLbl, BorderLayout.EAST);

        listPanel.add(row);
        listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
}
