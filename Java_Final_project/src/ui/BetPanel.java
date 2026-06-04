package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BetPanel extends JPanel {
    private final MainWindow window;
    private int localScore = 0;
    private int wagerPercent = 25;
    private int selectedMultiplier = 2;
    private int timeLeft = 30;
    private Timer betTimer;

    private final JLabel scoreLabel;
    private final JLabel wagerPreviewLabel;
    private final JLabel timerLabel;
    private final JButton submitBtn;

    private static final Color BG = new Color(24, 28, 48);
    private static final Color CARD_BG = new Color(36, 42, 73);
    private static final Color TEXT_COLOR = Color.WHITE;

    public BetPanel(MainWindow window) {
        this.window = window;
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Panel Components
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(BG);

        JLabel titleLabel = new JLabel("BETTING PHASE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(new Color(241, 196, 15));
        headerPanel.add(titleLabel);

        timerLabel = new JLabel("Time Remaining: 30", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        timerLabel.setForeground(Color.LIGHT_GRAY);
        headerPanel.add(timerLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Core Configuration Container Panel
        JPanel centerCard = new JPanel();
        centerCard.setLayout(new BoxLayout(centerCard, BoxLayout.Y_AXIS));
        centerCard.setBackground(CARD_BG);
        centerCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        scoreLabel = new JLabel("Your Current Balance: 0 pts", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLabel.setForeground(TEXT_COLOR);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerCard.add(scoreLabel);
        centerCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Wager Percentage Sliders/Selectors Configuration
        JLabel wagerTitle = new JLabel("Select Risk Percentage:", SwingConstants.CENTER);
        wagerTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        wagerTitle.setForeground(Color.LIGHT_GRAY);
        wagerTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerCard.add(wagerTitle);

        JPanel wagerGrid = new JPanel(new GridLayout(1, 4, 10, 0));
        wagerGrid.setBackground(CARD_BG);
        wagerGrid.setMaximumSize(new Dimension(350, 40));
        int[] percents = {25, 50, 75, 100};
        for (int pct : percents) {
            JButton pctBtn = new JButton(pct + "%");
            pctBtn.addActionListener(e -> {
                this.wagerPercent = pct;
                recalculateWagerMetrics();
            });
            wagerGrid.add(pctBtn);
        }
        centerCard.add(Box.createRigidArea(new Dimension(0, 10)));
        centerCard.add(wagerGrid);
        centerCard.add(Box.createRigidArea(new Dimension(0, 25)));

        // Multiplier Selection System
        JLabel multTitle = new JLabel("Choose Score Return Multiplier:", SwingConstants.CENTER);
        multTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        multTitle.setForeground(Color.LIGHT_GRAY);
        multTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerCard.add(multTitle);

        JPanel multGrid = new JPanel(new GridLayout(1, 3, 10, 0));
        multGrid.setBackground(CARD_BG);
        multGrid.setMaximumSize(new Dimension(350, 40));
        int[] multipliers = {2, 3, 4};
        for (int mult : multipliers) {
            JButton mBtn = new JButton("x" + mult);
            mBtn.addActionListener(e -> {
                this.selectedMultiplier = mult;
                recalculateWagerMetrics();
            });
            multGrid.add(mBtn);
        }
        centerCard.add(Box.createRigidArea(new Dimension(0, 10)));
        centerCard.add(multGrid);
        centerCard.add(Box.createRigidArea(new Dimension(0, 30)));

        // Live Calculated Readouts
        wagerPreviewLabel = new JLabel("Wager: 0 | Risk: 0 | Win Payout: 0", SwingConstants.CENTER);
        wagerPreviewLabel.setFont(new Font("Arial", Font.BOLD, 15));
        wagerPreviewLabel.setForeground(new Color(52, 152, 219));
        wagerPreviewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerCard.add(wagerPreviewLabel);

        add(centerCard, BorderLayout.CENTER);

        // Confirmation Footer Button Action
        submitBtn = new JButton("Lock In Bet");
        submitBtn.setFont(new Font("Arial", Font.BOLD, 18));
        submitBtn.setBackground(new Color(155, 89, 182));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.addActionListener(e -> fireBetToNetwork());
        
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(BG);
        footerPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        footerPanel.add(submitBtn, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void startBetRound(int currentScore, int nextQuestionIdx) {
        this.localScore = currentScore;
        this.wagerPercent = 25;
        this.selectedMultiplier = 2;
        this.timeLeft = 30;

        scoreLabel.setText("Your Current Balance: " + localScore + " pts");
        recalculateWagerMetrics();
        submitBtn.setEnabled(true);
        submitBtn.setText("Lock In Bet");

        if (betTimer != null && betTimer.isRunning()) betTimer.stop();
        betTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time Remaining: " + timeLeft);
            if (timeLeft <= 0) {
                fireBetToNetwork();
            }
        });
        betTimer.start();
    }

    private void recalculateWagerMetrics() {
        int calculatedWager = (int) (localScore * (wagerPercent / 100.0));
        int potentialGain = calculatedWager * (selectedMultiplier - 1);
        wagerPreviewLabel.setText(String.format("Betting: %d pts | Lose: -%d | Gain: +%d", 
                calculatedWager, calculatedWager, potentialGain));
    }

    private void fireBetToNetwork() {
        if (betTimer != null) betTimer.stop();
        submitBtn.setEnabled(false);
        submitBtn.setText("Waiting for others...");
        
        int finalWager = (int) (localScore * (wagerPercent / 100.0));
        window.submitPlayerBet(finalWager, selectedMultiplier);
    }
}