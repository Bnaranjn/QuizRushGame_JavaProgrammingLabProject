package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * RevealPanel: Shown after a question. Highlights the correct answer,
 * shows if player was right or wrong, and counts down to the next screen.
 */
public class RevealPanel extends JPanel {
    private final MainWindow window;
    private javax.swing.Timer countdownTimer;

    private final JLabel resultLabel;
    private final JLabel pointsLabel;
    private final JLabel correctAnsLabel;
    private final JButton[] optionBtns = new JButton[4];
    private final JLabel nextLabel;

    private static final Color BG      = new Color(24, 28, 48);
    private static final Color GOLD    = new Color(250, 199, 117);
    private static final Color MUTED   = new Color(107, 114, 153);
    private static final Color GREEN   = new Color(39, 174, 96);
    private static final Color RED     = new Color(231, 76, 60);
    private static final Color TEXT    = Color.WHITE;
    private static final Color CARD_BG = new Color(30, 35, 64);

    public RevealPanel(MainWindow window) {
        this.window = window;
        setBackground(BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(36, 44, 36, 44));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG);

        resultLabel = new JLabel("Correct!");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 34));
        resultLabel.setForeground(GREEN);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        pointsLabel = new JLabel("+0 pts");
        pointsLabel.setFont(new Font("Arial", Font.BOLD, 22));
        pointsLabel.setForeground(GOLD);
        pointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        correctAnsLabel = new JLabel("Correct answer: ");
        correctAnsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        correctAnsLabel.setForeground(MUTED);
        correctAnsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optionsPanel.setBackground(BG);
        optionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        String[] letters = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            optionBtns[i] = new JButton(letters[i]);
            optionBtns[i].setFont(new Font("Arial", Font.BOLD, 14));
            optionBtns[i].setForeground(TEXT);
            optionBtns[i].setFocusPainted(false);
            optionBtns[i].setBorderPainted(false);
            optionBtns[i].setEnabled(false);
            optionsPanel.add(optionBtns[i]);
        }

        nextLabel = new JLabel("Next in 3...");
        nextLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        nextLabel.setForeground(MUTED);
        nextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(resultLabel);
        center.add(Box.createRigidArea(new Dimension(0, 8)));
        center.add(pointsLabel);
        center.add(Box.createRigidArea(new Dimension(0, 20)));
        center.add(correctAnsLabel);
        center.add(Box.createRigidArea(new Dimension(0, 14)));
        center.add(optionsPanel);
        center.add(Box.createRigidArea(new Dimension(0, 28)));
        center.add(nextLabel);

        add(center, BorderLayout.CENTER);
    }

    public void showResult(int selectedAnswer, int correctAnswer,
                           String[] options, int pointsEarned, boolean correct) {
        boolean noAnswer = (selectedAnswer == -1);

        if (noAnswer) {
            resultLabel.setText("Time's up!");
            resultLabel.setForeground(MUTED);
        } else if (correct) {
            resultLabel.setText("Correct!");
            resultLabel.setForeground(GREEN);
        } else {
            resultLabel.setText("Wrong!");
            resultLabel.setForeground(RED);
        }

        pointsLabel.setText((pointsEarned >= 0 ? "+" : "") + pointsEarned + " pts");
        correctAnsLabel.setText("Correct answer: " + "ABCD".charAt(correctAnswer)
                                + "  " + options[correctAnswer]);

        // Color the option buttons
        Color[] dimmed = {
            new Color(140, 50, 30),
            new Color(30, 80, 130),
            new Color(140, 100, 0),
            new Color(30, 100, 40)
        };
        for (int i = 0; i < 4; i++) {
            String txt = "<html><center><b>" + "ABCD".charAt(i) + "</b>  "
                         + options[i] + "</center></html>";
            optionBtns[i].setText(txt);
            if (i == correctAnswer) {
                optionBtns[i].setBackground(new Color(39, 174, 96));
                optionBtns[i].setBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 2));
            } else if (i == selectedAnswer) {
                optionBtns[i].setBackground(new Color(192, 57, 43));
                optionBtns[i].setBorder(BorderFactory.createEmptyBorder());
            } else {
                optionBtns[i].setBackground(dimmed[i]);
                optionBtns[i].setBorder(BorderFactory.createEmptyBorder());
            }
        }

        startCountdown();
    }

    private void startCountdown() {
        if (countdownTimer != null) countdownTimer.stop();
        final int[] count = {3};
        nextLabel.setText("Next in 3...");

        countdownTimer = new javax.swing.Timer(1000, e -> {
            count[0]--;
            if (count[0] <= 0) {
                countdownTimer.stop();
                window.goToNextScreen();
            } else {
                nextLabel.setText("Next in " + count[0] + "...");
            }
        });
        countdownTimer.start();
    }

    public void stopCountdown() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
    }
}
