package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BetPanel extends JPanel {

    private MainWindow window;
    private int playerScore;
    private int selectedMult = 2;       // ×1, ×2, ×3
    private int wagerPercent = 25;      // 25, 50, 100 or custom

    //Labels that update live
    private JLabel winLabel;
    private JLabel loseLabel;
    private JLabel scoreDisplay;

    //Multiplier buttons
    private JButton[] multBtns;

    //Wager buttons
    private JButton[] wagerBtns;

    private static final Color BG        = new Color(24, 28, 48);
    private static final Color CARD_BG   = new Color(30, 35, 64);
    private static final Color GOLD      = new Color(250, 199, 117);
    private static final Color GREEN     = new Color(151, 196, 89);
    private static final Color RED       = new Color(240, 149, 123);
    private static final Color MUTED     = new Color(107, 114, 153);
    private static final Color TEXT      = Color.WHITE;

    public BetPanel(MainWindow window) {
        this.window = window;
        setBackground(BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(40, 50, 40, 50));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG);

        // Score row
        scoreDisplay = new JLabel("0 pts");
        scoreDisplay.setForeground(GOLD);
        scoreDisplay.setFont(new Font("Arial", Font.BOLD, 22));
        scoreDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLabel = new JLabel("your score");
        scoreLabel.setForeground(MUTED);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel title = new JLabel("Bet round");
        title.setForeground(TEXT);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Risk your points for a bigger reward");
        sub.setForeground(MUTED);
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Multiplier buttons
        JPanel multPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        multPanel.setBackground(BG);
        multPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        String[] multLabels = {"x1  Safe", "x2  Double", "x3  Triple"};
        multBtns = new JButton[3];
        for (int i = 0; i < 3; i++) {
            final int mult = i + 1;
            multBtns[i] = makeMultBtn(multLabels[i], mult);
            multPanel.add(multBtns[i]);
        }
       

        // Wager buttons
        JPanel wagerPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        wagerPanel.setBackground(BG);
        wagerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        String[] wagerLabels = {"25", "50", "All in", "Custom"};
        int[] wagerVals = {25, 50, 100, -1};
        wagerBtns = new JButton[4];
        for (int i = 0; i < 4; i++) {
            final int val = wagerVals[i];
            wagerBtns[i] = makeWagerBtn(wagerLabels[i]);
            wagerBtns[i].addActionListener(e -> {
                if (val == -1) {
                    // Custom ask with a dialog
                    String input = JOptionPane.showInputDialog(
                        window, "Enter wager  (1-100):", "Custom wager",
                        JOptionPane.PLAIN_MESSAGE);
                    try {
                        int custom = Integer.parseInt(input.trim());
                        wagerPercent = Math.max(1, Math.min(100, custom));
                    } catch (Exception ex) { return; }
                } else {
                    wagerPercent = val;
                }
                highlightWager(val);
                updatePreview();
            });
            wagerPanel.add(wagerBtns[i]);
        }
        

        // Win / lose preview
        winLabel  = new JLabel("+0 pts");
        loseLabel = new JLabel("-0 pts");
        winLabel.setFont(new Font("Arial", Font.BOLD, 18));
        loseLabel.setFont(new Font("Arial", Font.BOLD, 18));
        winLabel.setForeground(GREEN);
        loseLabel.setForeground(RED);
        
        highlightMult(selectedMult);
        highlightWager(25);
        

        JPanel previewPanel = new JPanel(new GridLayout(1, 2));
        previewPanel.setBackground(CARD_BG);
        previewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(42, 47, 74)),
            new EmptyBorder(12, 16, 12, 16)
        ));
        previewPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel winSide = new JPanel(new BorderLayout());
        winSide.setBackground(CARD_BG);
        winSide.add(makeSmallLabel("if correct"), BorderLayout.NORTH);
        winSide.add(winLabel, BorderLayout.CENTER);

        JPanel loseSide = new JPanel(new BorderLayout());
        loseSide.setBackground(CARD_BG);
        loseSide.add(makeSmallLabel("if wrong"), BorderLayout.NORTH);
        loseLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        loseSide.add(loseLabel, BorderLayout.CENTER);

        previewPanel.add(winSide);
        previewPanel.add(loseSide);

        // Action buttons
        JButton skipBtn = new JButton("Skip bet");
        skipBtn.setBackground(BG);
        skipBtn.setForeground(MUTED);
        skipBtn.setFont(new Font("Arial", Font.BOLD, 14));
        skipBtn.setFocusPainted(false);
        skipBtn.addActionListener(e -> window.showScreen(MainWindow.SCREEN_QUESTION));

        JButton betBtn = new JButton("Place bet ");
        betBtn.setBackground(GOLD);
        betBtn.setForeground(new Color(65, 36, 2));
        betBtn.setFont(new Font("Arial", Font.BOLD, 14));
        betBtn.setFocusPainted(false);
        betBtn.addActionListener(e -> confirmBet());

        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionPanel.setBackground(BG);
        actionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        actionPanel.add(skipBtn);
        actionPanel.add(betBtn);

        // Assemble
        center.add(scoreLabel);
        center.add(scoreDisplay);
        center.add(Box.createRigidArea(new Dimension(0, 20)));
        center.add(title);
        center.add(Box.createRigidArea(new Dimension(0, 6)));
        center.add(sub);
        center.add(Box.createRigidArea(new Dimension(0, 24)));
        center.add(sectionLabel("Choose multiplier"));
        center.add(Box.createRigidArea(new Dimension(0, 8)));
        center.add(multPanel);
        center.add(Box.createRigidArea(new Dimension(0, 20)));
        center.add(sectionLabel("How much to wager"));
        center.add(Box.createRigidArea(new Dimension(0, 8)));
        center.add(wagerPanel);
        center.add(Box.createRigidArea(new Dimension(0, 16)));
        center.add(previewPanel);
        center.add(Box.createRigidArea(new Dimension(0, 20)));
        center.add(actionPanel);

        add(center, BorderLayout.CENTER);
    }

    // Call this before showing the screen
    public void setScore(int score) {
        this.playerScore = score;
        scoreDisplay.setText(score + " pts");
        updatePreview();
    }

    private void confirmBet() {
        int wagered = (int)(playerScore * (wagerPercent / 100.0));
        // Store the bet — Member C will wire this to the Player object
        // e.g. currentPlayer.setPendingBet(wagered, selectedMult);
        window.showScreen(MainWindow.SCREEN_QUESTION);
    }

    private void updatePreview() {
        int wagered = (int)(playerScore * (wagerPercent / 100.0));
        int win = wagered * (selectedMult - 1);
        int lose = wagered;
        winLabel.setText("+" + win + " pts");
        loseLabel.setText("-" + lose + " pts");
    }

    private void highlightMult(int m) {
        selectedMult = m;
        for (int i = 0; i < 3; i++) {
            boolean sel = (i + 1) == m;
            multBtns[i].setBackground(sel ? new Color(37, 31, 16) : new Color(30, 35, 64));
            multBtns[i].setForeground(sel ? GOLD : TEXT);
        }
        updatePreview();
    }

    private void highlightWager(int val) {
        int[] vals = {25, 50, 100, -1};
        for (int i = 0; i < 4; i++) {
            boolean sel = vals[i] == val;
            wagerBtns[i].setBackground(sel ? new Color(37, 31, 16) : new Color(30, 35, 64));
            wagerBtns[i].setForeground(sel ? GOLD : MUTED);
        }
    }

    // Helper builders
    private JButton makeMultBtn(String label, int mult) {
        JButton btn = new JButton("<html><center>" + label + "</center></html>");
        btn.setBackground(new Color(30, 35, 64));
        btn.setForeground(TEXT);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(42, 47, 74)));
        btn.addActionListener(e -> highlightMult(mult));
        return btn;
    }

    private JButton makeWagerBtn(String label) {
        JButton btn = new JButton(label);
        btn.setBackground(new Color(30, 35, 64));
        btn.setForeground(MUTED);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(42, 47, 74)));
        return btn;
    }

    private JLabel makeSmallLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(MUTED);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        return l;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setForeground(MUTED);
        l.setFont(new Font("Arial", Font.PLAIN, 11));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
}