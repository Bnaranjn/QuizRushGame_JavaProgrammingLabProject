package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class QuestionPanel extends JPanel {
    private final MainWindow window;
    private final JLabel questionLabel;
    private final JLabel timerLabel;
    private final JButton[] optionButtons;

    private int timeLeft = 20;
    private Timer countdownTimer;

    private static final Color BG        = new Color(18, 18, 30);  
    private static final Color TOP_BG    = new Color(28, 28, 45);  
    private static final Color TEXT_COLOR = Color.WHITE;

    private static final Color[] ANSWER_COLORS = {
        new Color(220, 55,  65),   // red
        new Color(55,  120, 190),  // blue
        new Color(240, 170, 0),    // yellow
        new Color(60,  165, 70)    // green
    };

    public QuestionPanel(MainWindow window) {
        this.window = window;
        setLayout(new BorderLayout());
        setBackground(BG);
        // CHANGED: removed outer EmptyBorder

        // ── Top bar ───────────────────────────────────────────────────────────
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(TOP_BG);                          // CHANGED: distinct top bar colour
        topPanel.setBorder(new EmptyBorder(14, 20, 14, 20));     

        // CHANGED: added question number label on the left
        JLabel questionNumber = new JLabel("Question");
        questionNumber.setForeground(TEXT_COLOR);
        questionNumber.setFont(new Font("Arial", Font.BOLD, 17));
        topPanel.add(questionNumber, BorderLayout.WEST);

        // CHANGED: timer shows just the number (no "Time:" prefix)
        timerLabel = new JLabel("20", SwingConstants.RIGHT);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 30));    // CHANGED: was 22pt
        timerLabel.setForeground(new Color(255, 200, 0));        
        topPanel.add(timerLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ── Question text ─────────────────────────────────────────────────────
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(BG);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(40, 40, 40, 40));  

        questionLabel = new JLabel("<html><div style='text-align:center;'>Loading question...</div></html>", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 26)); // CHANGED: was 22pt
        questionLabel.setForeground(TEXT_COLOR);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(questionLabel);
        add(centerPanel, BorderLayout.CENTER);

        JPanel optionsGrid = new JPanel(new GridLayout(2, 2, 12, 12));
        optionsGrid.setBorder(new EmptyBorder(16, 16, 20, 16)); 
        optionsGrid.setBackground(BG);

        optionButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            final int choiceIndex = i;
            optionButtons[i] = new JButton();
            optionButtons[i].setFont(new Font("Arial", Font.BOLD, 18)); 
            optionButtons[i].setBackground(ANSWER_COLORS[i]);          
            optionButtons[i].setForeground(TEXT_COLOR);
            optionButtons[i].setFocusPainted(false);
            optionButtons[i].setBorder(BorderFactory.createEmptyBorder(24, 16, 24, 16)); // CHANGED: taller buttons
            optionButtons[i].addActionListener(e -> commitSelection(choiceIndex));
            optionsGrid.add(optionButtons[i]);
        }
        add(optionsGrid, BorderLayout.SOUTH);
    }

    public void setQuestionText(String text) {
        questionLabel.setText("<html><div style='text-align:center;'>" + text + "</div></html>");
    }

    public void setOptions(String a, String b, String c, String d) {
        optionButtons[0].setText(a);
        optionButtons[1].setText(b);
        optionButtons[2].setText(c);
        optionButtons[3].setText(d);
        setOptionsEnabled(true);
    }

    public void startTimer(int seconds) {
        stopTimer();
        this.timeLeft = seconds;
        timerLabel.setText(String.valueOf(timeLeft));            // CHANGED: no "Time:" prefix
        timerLabel.setForeground(new Color(255, 200, 0));        // reset to yellow each round
        countdownTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText(String.valueOf(timeLeft));        // CHANGED: plain number
            if (timeLeft <= 5) timerLabel.setForeground(Color.RED); // CHANGED: red warning like first panel
            if (timeLeft <= 0) {
                stopTimer();
                commitSelection(-1);
            }
        });
        countdownTimer.start();
    }

    public void stopTimer() {
        if (countdownTimer != null && countdownTimer.isRunning()) countdownTimer.stop();
    }

    private void commitSelection(int index) {
        stopTimer();
        setOptionsEnabled(false);
        timerLabel.setText("Locked In");                              
        window.submitPlayerAnswer(index, timeLeft);
    }

    private void setOptionsEnabled(boolean enabled) {
        for (JButton btn : optionButtons) btn.setEnabled(enabled);
    }
}