package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * QuestionPanel: Displays a question with 4 answer buttons and a countdown timer.
 *
 * Timer synchronization: The server broadcasts QUESTION|idx to all clients at once.
 * Each client starts its local timer when it receives that message, so timers are
 * in sync (off only by network latency, typically <50ms on LAN).
 */
public class QuestionPanel extends JPanel {
    private static final int QUESTION_TIME_SECS = 20;

    private final MainWindow window;
    private javax.swing.Timer questionTimer;
    private int timeLeft = QUESTION_TIME_SECS;
    private int selectedAnswer = -1;

    // Components
    private final JLabel timerLabel;
    private final JLabel questionNumber;
    private final JLabel questionText;
    private final JButton[] answerBtns;

    public QuestionPanel(MainWindow window) {
        this.window = window;
        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 30));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(28, 28, 45));
        topPanel.setBorder(new EmptyBorder(14, 20, 14, 20));

        questionNumber = new JLabel("Question 1/10");
        questionNumber.setForeground(Color.WHITE);
        questionNumber.setFont(new Font("Arial", Font.BOLD, 17));

        timerLabel = new JLabel(String.valueOf(QUESTION_TIME_SECS));
        timerLabel.setForeground(new Color(255, 200, 0));
        timerLabel.setFont(new Font("Arial", Font.BOLD, 30));

        topPanel.add(questionNumber, BorderLayout.WEST);
        topPanel.add(timerLabel,     BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(18, 18, 30));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        questionText = new JLabel("<html><div style='text-align:center;'>Question</div></html>");
        questionText.setForeground(Color.WHITE);
        questionText.setFont(new Font("Arial", Font.BOLD, 26));
        questionText.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(questionText);
        add(centerPanel, BorderLayout.CENTER);
        
        
        JPanel answerPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        answerPanel.setBorder(new EmptyBorder(16, 16, 20, 16));
        answerPanel.setBackground(new Color(18, 18, 30));

        Color[] colors = {
            new Color(220, 55, 65),
            new Color(55, 120, 190),
            new Color(240, 170, 0),
            new Color(60, 165, 70)
        };

        answerBtns = new JButton[4];
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            answerBtns[i] = createAnswerButton("Option " + (i + 1), colors[i]);
            answerBtns[i].addActionListener(e -> handleAnswer(idx));
            answerPanel.add(answerBtns[i]);
        }
        add(answerPanel, BorderLayout.SOUTH);
    }
    

    public void loadQuestion(String question, String[] options,
                             int qNum, int total, int score) {
        selectedAnswer = -1;
        questionNumber.setText("Question " + qNum + " / " + total
                               + "   |   Score: " + score);
        questionText.setText("<html><div style='text-align:center;'>"
                             + question + "</div></html>");
        for (int i = 0; i < 4; i++) {
            answerBtns[i].setText(options[i]);
            answerBtns[i].setEnabled(true);
        }
        startTimer();
    }

    public void stopTimer() {
        if (questionTimer != null && questionTimer.isRunning()) {
            questionTimer.stop();
        }
    }

    public int getTimeLeft()        { return timeLeft; }
    public int getSelectedAnswer()  { return selectedAnswer; }

    private void handleAnswer(int idx) {
        if (selectedAnswer != -1) return;  // already answered
        selectedAnswer = idx;
        stopTimer();
        for (JButton btn : answerBtns) btn.setEnabled(false);
        // Brief pause so player sees their selection before reveal
        new javax.swing.Timer(800, e -> {
            ((javax.swing.Timer) e.getSource()).stop();
            window.submitAnswer(idx);
        }).start();
    }

    private void startTimer() {
        stopTimer();
        timeLeft = QUESTION_TIME_SECS;
        timerLabel.setText(String.valueOf(QUESTION_TIME_SECS));
        timerLabel.setForeground(new Color(255, 200, 0));

        questionTimer = new javax.swing.Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText(String.valueOf(timeLeft));
            if (timeLeft <= 5) timerLabel.setForeground(Color.RED);
            if (timeLeft <= 0) {
                stopTimer();
                selectedAnswer = -1;
                window.submitAnswer(-1);   // -1 = no answer / timeout
            }
        });
        questionTimer.start();
    }

    private JButton createAnswerButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(24, 16, 24, 16));
        return btn;
    }
}
