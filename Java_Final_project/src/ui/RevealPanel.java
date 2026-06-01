package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RevealPanel extends JPanel {

    private MainWindow window;

    private JLabel resultLabel;    //"Correct!" or "Wrong!" or "Time's up!"
    private JLabel pointsLabel;    //"+760 pts" or "+0 pts"
    private JLabel correctAnsLabel;
    private JButton[] optionLabels;
    private JLabel nextLabel;      //"Next question in 3..."
    private javax.swing.Timer countdownTimer;

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
        setBorder(new EmptyBorder(40, 50, 40, 50));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG);

        //Result badge
        resultLabel = new JLabel("Correct!");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 32));
        resultLabel.setForeground(GREEN);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Points earned
        pointsLabel = new JLabel("+0 pts");
        pointsLabel.setFont(new Font("Arial", Font.BOLD, 22));
        pointsLabel.setForeground(GOLD);
        pointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Correct answer text
        correctAnsLabel = new JLabel("Correct answer: ");
        correctAnsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        correctAnsLabel.setForeground(MUTED);
        correctAnsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //4 option reveal buttons (colored)
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optionsPanel.setBackground(BG);
        optionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        optionLabels = new JButton[4];
        String[] letters = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            optionLabels[i] = new JButton(letters[i]);
            optionLabels[i].setFont(new Font("Arial", Font.BOLD, 14));
            optionLabels[i].setForeground(TEXT);
            optionLabels[i].setFocusPainted(false);
            optionLabels[i].setBorderPainted(false);
            optionLabels[i].setEnabled(false);
            optionsPanel.add(optionLabels[i]);
        }

        //Next countdown
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
        center.add(Box.createRigidArea(new Dimension(0, 30)));
        center.add(nextLabel);

        add(center, BorderLayout.CENTER);
    }

    
    public void showResult(int selectedAnswer, int correctAnswer,
                           String[] options, int pointsEarned) {

        boolean correct = (selectedAnswer == correctAnswer);
        boolean noAnswer = (selectedAnswer == -1);

        // Result label
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

        // Points
        pointsLabel.setText("+" + pointsEarned + " pts");

        // Correct answer text
        correctAnsLabel.setText("Correct answer: " + "ABCD".charAt(correctAnswer)
                                 + "  " + options[correctAnswer]);

        // Color the option buttons
        Color[] baseColors = {
            new Color(211, 84, 0),
            new Color(39, 174, 96),
            new Color(41, 128, 185),
            new Color(142, 68, 173)
        };

        for (int i = 0; i < 4; i++) {
            String optText = "<html><center><b>" + "ABCD".charAt(i)
                             + "</b>  " + options[i] + "</center></html>";
            optionLabels[i].setText(optText);

            if (i == correctAnswer) {
                // Correct = bright green
                optionLabels[i].setBackground(new Color(39, 174, 96));
                optionLabels[i].setBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 2));
            } else if (i == selectedAnswer) {
                // Wrong selection = red
                optionLabels[i].setBackground(new Color(192, 57, 43));
                optionLabels[i].setBorder(BorderFactory.createEmptyBorder());
            } else {
                // Unselected = dimmed
                optionLabels[i].setBackground(baseColors[i].darker().darker());
                optionLabels[i].setBorder(BorderFactory.createEmptyBorder());
            }
        }

        startCountdown();
    }

    
    private void startCountdown() {
        final int[] count = {3};
        nextLabel.setText("Next in 3...");

        countdownTimer = new javax.swing.Timer(1000, null);
        countdownTimer.addActionListener(e -> {
            count[0]--;
            if (count[0] <= 0) {
                countdownTimer.stop();
                window.goToNextScreen(); //MainWindow decides what's next
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