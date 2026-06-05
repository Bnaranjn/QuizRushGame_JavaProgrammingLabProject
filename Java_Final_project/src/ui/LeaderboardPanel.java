package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class LeaderboardPanel extends JPanel {
    private final JPanel layoutListContainer;

    public LeaderboardPanel(MainWindow window) {
        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 30));

        JLabel bannerTitle = new JLabel("CHAMPIONSHIP PODIUM", SwingConstants.CENTER);
        bannerTitle.setFont(new Font("Arial", Font.BOLD, 28));
        bannerTitle.setForeground(new Color(241, 196, 15));
        bannerTitle.setBorder(new EmptyBorder(25, 10, 20, 10));
        add(bannerTitle, BorderLayout.NORTH);

        layoutListContainer = new JPanel();
        layoutListContainer.setLayout(new BoxLayout(layoutListContainer, BoxLayout.Y_AXIS));
        layoutListContainer.setBackground(new Color(18, 18, 30));

        JScrollPane innerScrollArea = new JScrollPane(layoutListContainer);
        innerScrollArea.setBorder(BorderFactory.createEmptyBorder());
        innerScrollArea.getViewport().setBackground(new Color(18, 18, 30));
        add(innerScrollArea, BorderLayout.CENTER);

        JButton finalizeExitBtn = new JButton("Close");
        
        finalizeExitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        finalizeExitBtn.setFont(new Font("Arial", Font.BOLD, 16));
        finalizeExitBtn.setBackground(new Color(192, 41, 43));
        finalizeExitBtn.setForeground(Color.WHITE);
        finalizeExitBtn.setFocusPainted(false);
        finalizeExitBtn.setBorder(new EmptyBorder(12, 40, 12, 40));
        finalizeExitBtn.setMaximumSize(new Dimension(280, 50));
	    finalizeExitBtn.addActionListener(e -> System.exit(0));

        JPanel panelWrap = new JPanel();
        panelWrap.setBackground(new Color(18, 18, 30));
        panelWrap.setBorder(new EmptyBorder(15, 10, 15, 10));
        panelWrap.add(finalizeExitBtn);
        add(panelWrap, BorderLayout.SOUTH);
    }

    public void displayFinalStandings(Map<String, Integer> mapScores, String trackingSelfIdent) {
        SwingUtilities.invokeLater(() -> {
            layoutListContainer.removeAll();

            List<Map.Entry<String, Integer>> standingsList = new ArrayList<>(mapScores.entrySet());
            standingsList.sort((r1, r2) -> r2.getValue().compareTo(r1.getValue()));

            int operationalRank = 1;
            for (Map.Entry<String, Integer> rowNode : standingsList) {
                JPanel rowCard = new JPanel(new BorderLayout());
                rowCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
                boolean matchingIdent = rowNode.getKey().equals(trackingSelfIdent);

                rowCard.setBackground(matchingIdent ? new Color(41, 128, 185) : new Color(34, 41, 68));
                rowCard.setBorder(new EmptyBorder(10, 20, 10, 20));

                JLabel positionLabel = new JLabel("#" + operationalRank + "  ");
                positionLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
                positionLabel.setForeground(operationalRank == 1 ? new Color(241, 196, 15) : Color.WHITE);

                JLabel handleLabel = new JLabel(rowNode.getKey() + (matchingIdent ? " (You)" : ""));
                handleLabel.setFont(new Font("Arial", Font.BOLD, 16));
                handleLabel.setForeground(Color.WHITE);

                JLabel totalValueLabel = new JLabel(rowNode.getValue() + " pts");
                totalValueLabel.setFont(new Font("Arial", Font.BOLD, 16));
                totalValueLabel.setForeground(new Color(46, 204, 113));

                rowCard.add(positionLabel, BorderLayout.WEST);
                rowCard.add(handleLabel, BorderLayout.CENTER);
                rowCard.add(totalValueLabel, BorderLayout.EAST);

                layoutListContainer.add(rowCard);
                layoutListContainer.add(Box.createRigidArea(new Dimension(0, 8)));
                operationalRank++;
            }
            layoutListContainer.revalidate();
            layoutListContainer.repaint();
        });
    }
}