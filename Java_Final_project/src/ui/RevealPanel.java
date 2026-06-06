package ui;
//remodified on 6/6 

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class RevealPanel extends JPanel {

    private final MainWindow window;
    private final JLabel correctOptionLabel;
    private final JPanel listContainer;
    private final JButton nextBtn;

    private static final Color BG        = new Color(18, 18, 30);
    private static final Color CARD_BG   = new Color(34, 41, 68);
    private static final Color SELF_BG   = new Color(41, 82, 125);
    private static final Color GREEN     = new Color(46, 204, 113);
    private static final Color GOLD      = new Color(241, 196, 15);

    public RevealPanel(MainWindow window) {
        this.window = window;
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // title
        JLabel titleLabel = new JLabel("ROUND RESULTS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(GOLD);
        titleLabel.setBorder(new EmptyBorder(5, 10, 14, 10));
        add(titleLabel, BorderLayout.NORTH);

        // center: correct answer card &standings list
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BG);

        //correct answer 
        //modified 6.6
        JPanel correctCard = new JPanel(new BorderLayout(0, 4));
        correctCard.setBackground(new Color(26, 58, 42));
        correctCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GREEN, 1),
                new EmptyBorder(12, 20, 12, 20)));
        correctCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        correctCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel correctHint = new JLabel("CORRECT ANSWER", SwingConstants.CENTER);
        correctHint.setFont(new Font("Arial", Font.PLAIN, 11));
        correctHint.setForeground(new Color(127, 187, 159));

        correctOptionLabel = new JLabel("—", SwingConstants.CENTER);
        correctOptionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        correctOptionLabel.setForeground(GREEN);

        correctCard.add(correctHint, BorderLayout.NORTH);
        correctCard.add(correctOptionLabel, BorderLayout.CENTER);
        centerPanel.add(correctCard);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 14)));

        // list
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(BG);

        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        centerPanel.add(scroll);
        add(centerPanel, BorderLayout.CENTER);

        //footer button for the host
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

    public void showResults(String correctText, Map<String, Integer> currentStandings, String myName, boolean isHost) {
        SwingUtilities.invokeLater(() -> {
            nextBtn.setVisible(isHost);
            correctOptionLabel.setText(correctText);

            listContainer.removeAll();

            List<Map.Entry<String, Integer>> sorted = new ArrayList<>(currentStandings.entrySet());
            sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            int rank = 1;
            for (Map.Entry<String, Integer> entry : sorted) {
                boolean isSelf = entry.getKey().equals(myName);

                JPanel row = new JPanel(new BorderLayout());
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
                row.setBackground(isSelf ? SELF_BG : CARD_BG);
                row.setBorder(new EmptyBorder(10, 20, 10, 20));

                JLabel rankLabel = new JLabel("#" + rank + "  ");
                rankLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
                rankLabel.setForeground(rank == 1 ? GOLD : Color.WHITE);

                JLabel nameLabel = new JLabel(entry.getKey() + (isSelf ? " (You)" : ""));
                nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
                nameLabel.setForeground(Color.WHITE);

                JLabel scoreLabel = new JLabel(entry.getValue() + " pts");
                scoreLabel.setFont(new Font("Arial", Font.BOLD, 15));
                scoreLabel.setForeground(GREEN);

                row.add(rankLabel, BorderLayout.WEST);
                row.add(nameLabel, BorderLayout.CENTER);
                row.add(scoreLabel, BorderLayout.EAST);

                listContainer.add(row);
                listContainer.add(Box.createRigidArea(new Dimension(0, 8)));
                rank++;
            }

            listContainer.revalidate();
            listContainer.repaint();
        });
    }
}