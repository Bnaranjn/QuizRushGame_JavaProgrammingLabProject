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

    private static final Color BG = new Color(24, 28, 48);
    private static final Color BTN_BG = new Color(50, 60, 100);
    private static final Color TEXT_COLOR = Color.WHITE;

    public QuestionPanel(MainWindow window) {
        this.window = window;
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Status Header Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG);

        timerLabel = new JLabel("Time: 20", SwingConstants.RIGHT);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        timerLabel.setForeground(new Color(255, 75, 75));
        topPanel.add(timerLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Core Question Text Component
        questionLabel = new JLabel("<html><center>Loading question payload...</center></html>", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 22));
        questionLabel.setForeground(TEXT_COLOR);
        add(questionLabel, BorderLayout.CENTER);

        // Grid Layout containing selection choices
        JPanel optionsGrid = new JPanel(new GridLayout(4, 1, 10, 10));
        optionsGrid.setBackground(BG);
        optionsGrid.setPreferredSize(new Dimension(0, 260));

        optionButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            final int choiceIndex = i + 1; // 1-based indexing sequence matching file structure
            optionButtons[i] = new JButton();
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 18));
            optionButtons[i].setBackground(BTN_BG);
            optionButtons[i].setForeground(TEXT_COLOR);
            optionButtons[i].setFocusPainted(false);
            optionButtons[i].addActionListener(e -> commitSelection(choiceIndex));
            optionsGrid.add(optionButtons[i]);
        }

        add(optionsGrid, BorderLayout.SOUTH);
    }

    public void setQuestionText(String text) {
        questionLabel.setText("<html><center>" + text + "</center></html>");
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
        timerLabel.setText("Time: " + timeLeft);

        countdownTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time: " + timeLeft);
            if (timeLeft <= 0) {
                stopTimer();
                commitSelection(-1); // Automatically submit penalty timeout index
            }
        });
        countdownTimer.start();
    }

    public void stopTimer() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
    }

    private void commitSelection(int index) {
        stopTimer();
        setOptionsEnabled(false);
        timerLabel.setText("Locked In");
        window.submitPlayerAnswer(index, timeLeft);
    }

    private void setOptionsEnabled(boolean enabled) {
        for (JButton btn : optionButtons) {
            btn.setEnabled(enabled);
        }
    }
}