package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * BetPanel: Betting round screen shown after every 3rd question.
 * Fixed: startBetRound() replaces the missing startTimer() that was called
 * but never defined; bet is wired to MainWindow.placeBet(); "Skip" also
 * notifies MainWindow so the host advances the game.
 */
public class BetPanel extends JPanel {
    private final MainWindow window;

    private int playerScore   = 0;
    private int nextQuestionIndex = 0;  // which question to go to after betting
    private int selectedMult  = 2;
    private int wagerPercent  = 25;

    // Bet-round countdown (30 s to place a bet)
    private static final int BET_TIME_SECS = 30;
    private int betTimeLeft = BET_TIME_SECS;
    private javax.swing.Timer betTimer;

    // Labels that update live
    private final JLabel winLabel;
    private final JLabel loseLabel;
    private final JLabel scoreDisplay;
    private final JLabel timerLabel;
    private final JButton[] multBtns;
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
        
        scoreDisplay = new JLabel("0 pts");
        scoreDisplay.setForeground(GOLD);
        scoreDisplay.setFont(new Font("Arial", Font.BOLD, 22));
        scoreDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLabel = new JLabel("your current score");
        scoreLabel.setForeground(MUTED);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        timerLabel = new JLabel("30");
        timerLabel.setForeground(GOLD);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel title = new JLabel("Bet Round!");
        title.setForeground(TEXT);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Risk your points for a bigger reward");
        sub.setForeground(MUTED);
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel multPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        multPanel.setBackground(BG);
        multPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        String[] multLabels = {"x1  Safe", "x2  Double", "x3  Triple"};
        multBtns = new JButton[3];
        for (int i = 0; i < 3; i++) {
            final int mult = i + 1;
            multBtns[i] = makeMultBtn(multLabels[i], mult);
            final int ii = i;
            multBtns[i].addActionListener(e -> {
                selectedMult = mult;
                highlightMult(mult);
                updatePreview();
            });
            multPanel.add(multBtns[i]);
        }
        
        JPanel wagerPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        wagerPanel.setBackground(BG);
        wagerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        String[] wagerLabels = {"25%", "50%", "All in", "Custom"};
        int[]    wagerVals   = {25, 50, 100, -1};
        wagerBtns = new JButton[4];
        for (int i = 0; i < 4; i++) {
            final int val = wagerVals[i];
            wagerBtns[i] = makeWagerBtn(wagerLabels[i]);
            wagerBtns[i].addActionListener(e -> {
                if (val == -1) {
                    String input = JOptionPane.showInputDialog(
                        window, "Enter wager % (1–100):", "Custom Wager",
                        JOptionPane.PLAIN_MESSAGE);
                    if (input == null) return;
                    try {
                        int custom = Integer.parseInt(input.trim());
                        wagerPercent = Math.max(1, Math.min(100, custom));
                    } catch (NumberFormatException ex) { return; }
                } else {
                    wagerPercent = val;
                }
                highlightWager(val);
                updatePreview();
            });
            wagerPanel.add(wagerBtns[i]);
        }
        
        winLabel  = new JLabel("+0 pts");
        loseLabel = new JLabel("-0 pts");
        winLabel.setFont(new Font("Arial", Font.BOLD, 18));
        loseLabel.setFont(new Font("Arial", Font.BOLD, 18));
        winLabel.setForeground(GREEN);
        loseLabel.setForeground(RED);
        loseLabel.setHorizontalAlignment(SwingConstants.RIGHT);

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
        
        JButton skipBtn = new JButton("Skip bet");
        skipBtn.setBackground(BG);
        skipBtn.setForeground(MUTED);
        skipBtn.setFont(new Font("Arial", Font.BOLD, 14));
        skipBtn.setFocusPainted(false);
        skipBtn.addActionListener(e -> skipBet());

        JButton betBtn = new JButton("Place Bet >>");
        betBtn.setBackground(GOLD);
        betBtn.setForeground(new Color(65, 36, 2));
        betBtn.setFont(new Font("Arial", Font.BOLD, 14));
        betBtn.setFocusPainted(false);
        betBtn.addActionListener(e -> confirmBet());

        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionPanel.setBackground(BG);
        actionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        actionPanel.add(skipBtn);
        actionPanel.add(betBtn);
        
        center.add(scoreLabel);
        center.add(scoreDisplay);
        center.add(Box.createRigidArea(new Dimension(0, 6)));
        center.add(timerLabel);
        center.add(Box.createRigidArea(new Dimension(0, 16)));
        center.add(title);
        center.add(Box.createRigidArea(new Dimension(0, 5)));
        center.add(sub);
        center.add(Box.createRigidArea(new Dimension(0, 20)));
        center.add(sectionLabel("Choose multiplier"));
        center.add(Box.createRigidArea(new Dimension(0, 8)));
        center.add(multPanel);
        center.add(Box.createRigidArea(new Dimension(0, 18)));
        center.add(sectionLabel("How much to wager"));
        center.add(Box.createRigidArea(new Dimension(0, 8)));
        center.add(wagerPanel);
        center.add(Box.createRigidArea(new Dimension(0, 14)));
        center.add(previewPanel);
        center.add(Box.createRigidArea(new Dimension(0, 18)));
        center.add(actionPanel);
        add(center, BorderLayout.CENTER);

        highlightMult(selectedMult);
        highlightWager(25);
    }
    

    /**
     * Called from MainWindow when BET_ROUND message arrives.
     * Sets the score display, resets selections, and starts the bet timer.
     */
    public void startBetRound(int score, int nextQIdx) {
        this.playerScore       = score;
        this.nextQuestionIndex = nextQIdx;
        this.selectedMult      = 2;
        this.wagerPercent      = 25;
        scoreDisplay.setText(score + " pts");
        highlightMult(2);
        highlightWager(25);
        updatePreview();
        startBetTimer();
    }

    private void startBetTimer() {
        if (betTimer != null) betTimer.stop();
        betTimeLeft = BET_TIME_SECS;
        timerLabel.setText(String.valueOf(BET_TIME_SECS));
        timerLabel.setForeground(GOLD);

        betTimer = new javax.swing.Timer(1000, e -> {
            betTimeLeft--;
            timerLabel.setText(String.valueOf(betTimeLeft));
            if (betTimeLeft <= 10) timerLabel.setForeground(RED);
            if (betTimeLeft <= 0) {
                betTimer.stop();
                skipBet();   // auto-skip when time runs out
            }
        });
        betTimer.start();
    }

    private void confirmBet() {
        if (betTimer != null) betTimer.stop();
        int wagered = (int)(playerScore * (wagerPercent / 100.0));
        window.placeBet(wagered, selectedMult);
        window.betDone(nextQuestionIndex);
    }

    private void skipBet() {
        if (betTimer != null) betTimer.stop();
        window.placeBet(0, 1);          // no bet
        window.betDone(nextQuestionIndex);
    }

    private void updatePreview() {
        int wagered = (int)(playerScore * (wagerPercent / 100.0));
        int win  = wagered * (selectedMult - 1);
        int lose = wagered;
        winLabel.setText("+" + win + " pts");
        loseLabel.setText("-" + lose + " pts");
    }
    

    private JButton makeMultBtn(String text, int mult) {
        JButton btn = new JButton("<html><center>" + text + "</center></html>");
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(TEXT);
        btn.setBackground(new Color(42, 47, 80));
        btn.setFocusPainted(false);
        return btn;
    }

    private JButton makeWagerBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(TEXT);
        btn.setBackground(new Color(42, 47, 80));
        btn.setFocusPainted(false);
        return btn;
    }

    private void highlightMult(int mult) {
        for (int i = 0; i < multBtns.length; i++) {
            boolean sel = (i + 1 == mult);
            multBtns[i].setBackground(sel ? new Color(90, 120, 255) : new Color(42, 47, 80));
        }
    }

    private void highlightWager(int val) {
        int[] vals = {25, 50, 100, -1};
        for (int i = 0; i < wagerBtns.length; i++) {
            boolean sel = (vals[i] == val);
            wagerBtns[i].setBackground(sel ? new Color(90, 120, 255) : new Color(42, 47, 80));
        }
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
