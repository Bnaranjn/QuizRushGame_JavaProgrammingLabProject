package ui;
// last modified on 6/6

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

// This panel shows the correct answer and the current standings after each question
// The host also gets a "Next" button to advance the game
public class RevealPanel extends JPanel {

    private final MainWindow window;
    private final JLabel correctOptionLabel; // displays the correct answer text
    private final JPanel listContainer;      // holds the ranked player rows
    private final JButton nextBtn;           // only visible to the host

    // Color constants used throughout the panel
    private static final Color BG      = new Color(18, 18, 30);
    private static final Color CARD_BG = new Color(34, 41, 68);  // default player row background
    private static final Color SELF_BG = new Color(41, 82, 125); // highlighted background for the current player
    private static final Color GREEN   = new Color(46, 204, 113);
    private static final Color GOLD    = new Color(241, 196, 15);

    public RevealPanel(MainWindow window) {
        this.window = window;
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title bar at the top
        JLabel titleLabel = new JLabel("ROUND RESULTS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(GOLD);
        titleLabel.setBorder(new EmptyBorder(5, 10, 14, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Center section holds the correct answer card and the standings list
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BG);

        // Green card showing the correct answer for this round
        JPanel correctCard = new JPanel(new BorderLayout(0, 4));
        correctCard.setBackground(new Color(26, 58, 42)); // dark green background
        correctCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GREEN, 1),
                new EmptyBorder(12, 20, 12, 20)));
        correctCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        correctCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Small label above the answer text
        JLabel correctHint = new JLabel("CORRECT ANSWER", SwingConstants.CENTER);
        correctHint.setFont(new Font("Arial", Font.PLAIN, 11));
        correctHint.setForeground(new Color(127, 187, 159));

        // The actual correct answer — updated each round via showResults()
        correctOptionLabel = new JLabel("—", SwingConstants.CENTER);
        correctOptionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        correctOptionLabel.setForeground(GREEN);

        correctCard.add(correctHint, BorderLayout.NORTH);
        correctCard.add(correctOptionLabel, BorderLayout.CENTER);
        centerPanel.add(correctCard);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 14)));

        // Scrollable list of player rows sorted by score
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(BG);

        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        centerPanel.add(scroll);
        add(centerPanel, BorderLayout.CENTER);

        // "Next" button at the bottom — only the host can see and use this
        nextBtn = new JButton("Next");
        nextBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextBtn.setFont(new Font("Arial", Font.BOLD, 16));
        nextBtn.setBackground(new Color(90, 120, 255));
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFocusPainted(false);
        nextBtn.setBorder(new EmptyBorder(12, 40, 12, 40));
        nextBtn.setMaximumSize(new Dimension(280, 50));
        nextBtn.addActionListener(e -> window.hostRequestsNextPhase());

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(BG);
        footerPanel.setBorder(new EmptyBorder(14, 0, 0, 0));
        footerPanel.add(nextBtn, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    // Called after each question to update the correct answer and refresh the standings list
    public void showResults(String correctText, Map<String, Integer> currentStandings, String myName, boolean isHost) {
        SwingUtilities.invokeLater(() -> {
            nextBtn.setVisible(isHost); // only show Next button to the host
            correctOptionLabel.setText(correctText);

            listContainer.removeAll(); // clear previous round's rows

            // Sort players from highest to lowest score
            List<Map.Entry<String, Integer>> sorted = new ArrayList<>(currentStandings.entrySet());
            sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            int rank = 1;
            for (Map.Entry<String, Integer> entry : sorted) {
                boolean isSelf = entry.getKey().equals(myName); // highlight the current player's row

                JPanel row = new JPanel(new BorderLayout());
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
                row.setBackground(isSelf ? SELF_BG : CARD_BG); // blue tint for yourself
                row.setBorder(new EmptyBorder(10, 20, 10, 20));

                // Rank number — gold for first place, white for everyone else
                JLabel rankLabel = new JLabel("#" + rank + "  ");
                rankLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
                rankLabel.setForeground(rank == 1 ? GOLD : Color.WHITE);

                // Player name with "(You)" appended if it's the current player
                JLabel nameLabel = new JLabel(entry.getKey() + (isSelf ? " (You)" : ""));
                nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
                nameLabel.setForeground(Color.WHITE);

                // Score shown in green on the right side of the row
                JLabel scoreLabel = new JLabel(entry.getValue() + " pts");
                scoreLabel.setFont(new Font("Arial", Font.BOLD, 15));
                scoreLabel.setForeground(GREEN);

                row.add(rankLabel, BorderLayout.WEST);
                row.add(nameLabel, BorderLayout.CENTER);
                row.add(scoreLabel, BorderLayout.EAST);

                listContainer.add(row);
                listContainer.add(Box.createRigidArea(new Dimension(0, 8))); // spacing between rows
                rank++;
            }

            listContainer.revalidate();
            listContainer.repaint();
        });
    }
}