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
    private final JButton[] wagerBtns;

    private static final Color BG      = new Color(24, 28, 48);
    private static final Color CARD_BG = new Color(30, 35, 64);
    private static final Color GOLD    = new Color(250, 199, 117);
    private static final Color GREEN   = new Color(151, 196, 89);
    private static final Color RED     = new Color(240, 149, 123);
    private static final Color MUTED   = new Color(107, 114, 153);
    private static final Color TEXT    = Color.WHITE;

    public BetPanel(MainWindow window) {
        this.window = window;
        setBackground(BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 44, 30, 44));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG);

        // Score display
        scoreLabel = new JLabel("0 pts");
        scoreLabel.setForeground(GOLD);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 22));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreSub = new JLabel("your current score");
        scoreSub.setForeground(MUTED);
        scoreSub.setFont(new Font("Arial", Font.PLAIN, 12));
        scoreSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Timer
        timerLabel = new JLabel("30");
        timerLabel.setForeground(GOLD);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel title = new JLabel("Bet Round!");
        title.setForeground(TEXT);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Risk your points for a bigger reward");
        sub.setForeground(MUTED);
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Wager buttons
        JPanel wagerPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        wagerPanel.setBackground(BG);
        wagerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        int[] percents = {25, 50, 75, 100};
        String[] wagerLabels = {"25%", "50%", "75%", "All in"};
        wagerBtns = new JButton[4];
        for (int i = 0; i < percents.length; i++) {
            final int pct = percents[i];
            wagerBtns[i] = makeWagerBtn(wagerLabels[i]);
            wagerBtns[i].addActionListener(e -> {
                wagerPercent = pct;
                highlightWager(pct);
                recalculateWagerMetrics();
            });
            wagerPanel.add(wagerBtns[i]);
        }

        // Win/lose preview card
        JLabel winLabel  = new JLabel("+0 pts");
        JLabel loseLabel = new JLabel("-0 pts");
        winLabel.setFont(new Font("Arial", Font.BOLD, 18));
        loseLabel.setFont(new Font("Arial", Font.BOLD, 18));
        winLabel.setForeground(GREEN);
        loseLabel.setForeground(RED);
        loseLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Store references so recalculateWagerMetrics can update them
        this.wagerPreviewLabel = new JLabel(); // kept for interface compatibility
        this.wagerPreviewLabel.setVisible(false);

        // We update win/lose labels directly, so keep them accessible via a wrapper trick:
        // Instead, replace wagerPreviewLabel usage with a custom update approach below.
        // See recalculateWagerMetrics() — we repurpose winLabel/loseLabel via fields.
        this.winDisplayLabel  = winLabel;
        this.loseDisplayLabel = loseLabel;

        JPanel previewPanel = new JPanel(new GridLayout(1, 2));
        previewPanel.setBackground(CARD_BG);
        previewPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(42, 47, 74)),
                new EmptyBorder(10, 14, 10, 14)));
        previewPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JPanel winSide = new JPanel(new BorderLayout());
        winSide.setBackground(CARD_BG);
        winSide.add(makeSmallLabel("if correct"),  BorderLayout.NORTH);
        winSide.add(winLabel, BorderLayout.CENTER);

        JPanel loseSide = new JPanel(new BorderLayout());
        loseSide.setBackground(CARD_BG);
        loseSide.add(makeSmallLabel("if wrong"), BorderLayout.NORTH);
        loseSide.add(loseLabel, BorderLayout.CENTER);

        previewPanel.add(winSide);
        previewPanel.add(loseSide);

        // Skip + Place bet buttons
        JButton skipBtn = new JButton("Skip bet");
        skipBtn.setBackground(BG);
        skipBtn.setForeground(MUTED);
        skipBtn.setFont(new Font("Arial", Font.BOLD, 14));
        skipBtn.setFocusPainted(false);
        skipBtn.addActionListener(e -> {
            if (betTimer != null) betTimer.stop();
            wagerPercent = 0;
            fireBetToNetwork();
        });

        submitBtn = new JButton("Place Bet >>");
        submitBtn.setBackground(GOLD);
        submitBtn.setForeground(new Color(65, 36, 2));
        submitBtn.setFont(new Font("Arial", Font.BOLD, 14));
        submitBtn.setFocusPainted(false);
        submitBtn.addActionListener(e -> fireBetToNetwork());

        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionPanel.setBackground(BG);
        actionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        actionPanel.add(skipBtn);
        actionPanel.add(submitBtn);

        // Assemble
        center.add(scoreSub);
        center.add(scoreLabel);
        center.add(Box.createRigidArea(new Dimension(0, 6)));
        center.add(timerLabel);
        center.add(Box.createRigidArea(new Dimension(0, 16)));
        center.add(title);
        center.add(Box.createRigidArea(new Dimension(0, 5)));
        center.add(sub);
        center.add(Box.createRigidArea(new Dimension(0, 20)));
        center.add(sectionLabel("How much to wager"));
        center.add(Box.createRigidArea(new Dimension(0, 8)));
        center.add(wagerPanel);
        center.add(Box.createRigidArea(new Dimension(0, 14)));
        center.add(previewPanel);
        center.add(Box.createRigidArea(new Dimension(0, 18)));
        center.add(actionPanel);
        add(center, BorderLayout.CENTER);

        highlightWager(25);
    }

    // Extra label fields for the win/lose preview
    private JLabel winDisplayLabel;
    private JLabel loseDisplayLabel;

    public void startBetRound(int currentScore, int nextQuestionIdx) {
        this.localScore = currentScore;
        this.wagerPercent = 25;
        this.selectedMultiplier = 2;
        this.timeLeft = 30;

        scoreLabel.setText(localScore + " pts");
        highlightWager(25);
        recalculateWagerMetrics();
        submitBtn.setEnabled(true);
        submitBtn.setText("Place Bet >>");

        if (betTimer != null && betTimer.isRunning()) betTimer.stop();
        betTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText(String.valueOf(timeLeft));
            if (timeLeft <= 10) timerLabel.setForeground(RED);
            if (timeLeft <= 0) {
                betTimer.stop();
                fireBetToNetwork();
            }
        });
        betTimer.start();
    }

    private void recalculateWagerMetrics() {
        int wagered     = (int)(localScore * (wagerPercent / 100.0));
        int gain        = wagered * (selectedMultiplier - 1);
        winDisplayLabel.setText("+" + gain + " pts");
        loseDisplayLabel.setText("-" + wagered + " pts");
    }

    private void fireBetToNetwork() {
        if (betTimer != null) betTimer.stop();
        submitBtn.setEnabled(false);
        submitBtn.setText("Waiting for others...");

        int finalWager = (int)(localScore * (wagerPercent / 100.0));
        window.submitPlayerBet(finalWager, selectedMultiplier);
    }

    private void highlightWager(int val) {
        int[] vals = {25, 50, 75, 100};
        for (int i = 0; i < wagerBtns.length; i++) {
            boolean sel = (vals[i] == val);
            wagerBtns[i].setBackground(sel ? new Color(90, 120, 255) : new Color(42, 47, 80));
        }
    }

    private JButton makeWagerBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(TEXT);
        btn.setBackground(new Color(42, 47, 80));
        btn.setFocusPainted(false);
        return btn;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(MUTED);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel makeSmallLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(MUTED);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        return lbl;
    }
}