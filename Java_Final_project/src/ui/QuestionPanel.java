package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// This panel shows the current question and four answer buttons to the player
// It also runs a countdown timer and auto-submits if time runs out
public class QuestionPanel extends JPanel {
    private final MainWindow window;
    private final JLabel questionLabel;
    private final JLabel timerLabel;
    private final JButton[] optionButtons;

    private int timeLeft = 20;
    private Timer countdownTimer;

    // Color constants to keep the theme consistent across the panel
    private static final Color BG         = new Color(18, 18, 30);  // main background
    private static final Color TOP_BG     = new Color(28, 28, 45);  // top bar background
    private static final Color TEXT_COLOR = Color.WHITE;

    // Each answer button gets its own distinct color (red, blue, yellow, green)
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

        // Top bar holds the "Question" label on the left and the timer on the right
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(TOP_BG);
        topPanel.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel questionNumber = new JLabel("Question");
        questionNumber.setForeground(TEXT_COLOR);
        questionNumber.setFont(new Font("Arial", Font.BOLD, 17));
        topPanel.add(questionNumber, BorderLayout.WEST);

        // Timer label starts at 20 and counts down each second
        timerLabel = new JLabel("20", SwingConstants.RIGHT);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 30));
        timerLabel.setForeground(new Color(255, 200, 0)); // yellow by default
        topPanel.add(timerLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center area displays the question text
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(BG);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        questionLabel = new JLabel("<html><div style='text-align:center;'>Loading question...</div></html>", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 26));
        questionLabel.setForeground(TEXT_COLOR);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(questionLabel);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom grid holds the four answer buttons in a 2x2 layout
        JPanel optionsGrid = new JPanel(new GridLayout(2, 2, 12, 12));
        optionsGrid.setBorder(new EmptyBorder(16, 16, 20, 16));
        optionsGrid.setBackground(BG);

        optionButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            final int choiceIndex = i;
            optionButtons[i] = new JButton();
            optionButtons[i].setFont(new Font("Arial", Font.BOLD, 18));
            optionButtons[i].setBackground(ANSWER_COLORS[i]); // each button gets its own color
            optionButtons[i].setForeground(TEXT_COLOR);
            optionButtons[i].setFocusPainted(false);
            optionButtons[i].setBorder(BorderFactory.createEmptyBorder(24, 16, 24, 16)); // taller buttons
            optionButtons[i].addActionListener(e -> commitSelection(choiceIndex));
            optionsGrid.add(optionButtons[i]);
        }
        add(optionsGrid, BorderLayout.SOUTH);
    }

    // Wraps the question text in centered HTML so it renders properly in the label
    public void setQuestionText(String text) {
        questionLabel.setText("<html><div style='text-align:center;'>" + text + "</div></html>");
    }

    // Sets the text on each of the four answer buttons and re-enables them
    public void setOptions(String a, String b, String c, String d) {
        optionButtons[0].setText(a);
        optionButtons[1].setText(b);
        optionButtons[2].setText(c);
        optionButtons[3].setText(d);
        setOptionsEnabled(true);
    }

    // Starts the countdown timer from the given number of seconds
    // Turns the timer red when 5 seconds are left, and auto-submits at 0
    public void startTimer(int seconds) {
        stopTimer();
        this.timeLeft = seconds;
        timerLabel.setText(String.valueOf(timeLeft));
        timerLabel.setForeground(new Color(255, 200, 0)); // reset to yellow
        countdownTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText(String.valueOf(timeLeft));
            if (timeLeft <= 5) timerLabel.setForeground(Color.RED); // red warning when almost out of time
            if (timeLeft <= 0) {
                stopTimer();
                commitSelection(-1); // -1 means no answer was selected
            }
        });
        countdownTimer.start();
    }

    // Stops the timer if it's currently running
    public void stopTimer() {
        if (countdownTimer != null && countdownTimer.isRunning()) countdownTimer.stop();
    }

    // Locks in the player's answer, disables the buttons, and sends the result to the server
    private void commitSelection(int index) {
        stopTimer();
        setOptionsEnabled(false);
        timerLabel.setText("Locked In");
        window.submitPlayerAnswer(index, timeLeft);
    }

    // Enables or disables all four answer buttons at once
    private void setOptionsEnabled(boolean enabled) {
        for (JButton btn : optionButtons) btn.setEnabled(enabled);
    }
}