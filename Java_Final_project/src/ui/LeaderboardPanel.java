package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LeaderboardPanel extends JPanel {

    private JPanel leaderboardList;

    public LeaderboardPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 30));

        // ================= TITLE =================

        JLabel title = new JLabel("Leaderboard", SwingConstants.CENTER);

        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);

        title.setBorder(new EmptyBorder(30, 10, 20, 10));

        add(title, BorderLayout.NORTH);

        // ================= PLAYER LIST =================

        leaderboardList = new JPanel();
        leaderboardList.setLayout(new BoxLayout(leaderboardList, BoxLayout.Y_AXIS));

        leaderboardList.setBackground(new Color(18, 18, 30));

        leaderboardList.setBorder(new EmptyBorder(20, 40, 20, 40));

        // sample players
        addPlayer("Alice", 950, 1);
        addPlayer("Bob", 800, 2);
        addPlayer("Carol", 720, 3);
        addPlayer("David", 600, 4);

        add(leaderboardList, BorderLayout.CENTER);

        // ================= BUTTON =================

        JButton nextButton = new JButton("Next Question");

        nextButton.setFont(new Font("Arial", Font.BOLD, 22));
        nextButton.setBackground(new Color(90, 120, 255));
        nextButton.setForeground(Color.WHITE);

        nextButton.setFocusPainted(false);

        nextButton.setBorder(
                BorderFactory.createEmptyBorder(18, 30, 18, 30)
        );

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(18, 18, 30));

        bottomPanel.setBorder(new EmptyBorder(20, 20, 30, 20));

        bottomPanel.add(nextButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // reusable method
    private void addPlayer(String name, int score, int rank) {

        JPanel row = new JPanel(new BorderLayout());

        row.setMaximumSize(new Dimension(500, 70));

        row.setBackground(new Color(35, 35, 55));

        row.setBorder(new EmptyBorder(15, 20, 15, 20));

        // medal/rank
        JLabel rankLabel;

        if(rank == 1) {
        	//try using icon for the first 3
            rankLabel = new JLabel("First:");
        }
        else if(rank == 2) {
            rankLabel = new JLabel("Second:");
        }
        else if(rank == 3) {
            rankLabel = new JLabel("Third:");
        }
        else {
            rankLabel = new JLabel("#" + rank);
        }

        rankLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // player name
        JLabel nameLabel = new JLabel(name);

        nameLabel.setForeground(Color.WHITE);

        nameLabel.setFont(new Font("Arial", Font.BOLD, 22));

        // score
        JLabel scoreLabel = new JLabel(score + " pts");

        scoreLabel.setForeground(new Color(255, 215, 0));

        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));

        row.add(rankLabel, BorderLayout.WEST);
        row.add(nameLabel, BorderLayout.CENTER);
        row.add(scoreLabel, BorderLayout.EAST);

        leaderboardList.add(row);

        leaderboardList.add(Box.createRigidArea(new Dimension(0, 15)));
    }
}