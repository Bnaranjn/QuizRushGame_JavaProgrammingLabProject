package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class QuestionPanel extends JPanel {

    private MainWindow window;

    // Timer
    private javax.swing.Timer questionTimer;
    private int timeLeft = 20;

    // Components
    private JLabel timerLabel;
    private JLabel questionNumber;
    private JLabel questionText;

    private JButton[] answerBtns;

    private int selectedAnswer = -1;

    public QuestionPanel(MainWindow window) {

        this.window = window;

        setLayout(new BorderLayout());
        setBackground(new Color(18,18,30));

        //---------------- TOP PANEL ----------------//

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(28,28,45));
        topPanel.setBorder(new EmptyBorder(15,20,15,20));

        questionNumber = new JLabel("Question 1/10");
        questionNumber.setForeground(Color.WHITE);
        questionNumber.setFont(new Font("Arial", Font.BOLD, 18));

        timerLabel = new JLabel("20");
        timerLabel.setForeground(new Color(255,200,0));
        timerLabel.setFont(new Font("Arial", Font.BOLD, 28));

        topPanel.add(questionNumber, BorderLayout.WEST);
        topPanel.add(timerLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        //---------------- CENTER PANEL ----------------//

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(18,18,30));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(50,40,50,40));

        questionText = new JLabel(
            "<html><div style='text-align:center;'>Question Text</div></html>"
        );

        questionText.setForeground(Color.WHITE);
        questionText.setFont(new Font("Arial", Font.BOLD, 30));
        questionText.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(questionText);

        add(centerPanel, BorderLayout.CENTER);

        //---------------- ANSWERS ----------------//

        JPanel answerPanel = new JPanel(new GridLayout(2,2,15,15));
        answerPanel.setBorder(new EmptyBorder(20,20,20,20));
        answerPanel.setBackground(new Color(18,18,30));

        answerBtns = new JButton[4];

        Color[] colors = {
            new Color(230,57,70),
            new Color(69,123,157),
            new Color(255,183,3),
            new Color(76,175,80)
        };

        for(int i=0;i<4;i++) {

            final int idx = i;

            answerBtns[i] = createAnswerButton(
                "Answer " + (i + 1),
                colors[i]
            );

            answerBtns[i].addActionListener(e -> handleAnswer(idx));

            answerPanel.add(answerBtns[i]);
        }

        add(answerPanel, BorderLayout.SOUTH);
    }

    //------------------------------------------
    // Load Question
    //------------------------------------------

    public void loadQuestion(
            String question,
            String[] options,
            int currentQuestion,
            int totalQuestions,
            int score) {

        selectedAnswer = -1;

        questionNumber.setText(
            "Question " + currentQuestion + "/" + totalQuestions
        );

        questionText.setText(
            "<html><div style='text-align:center;'>"
            + question +
            "</div></html>"
        );

        for(int i=0;i<4;i++) {
            answerBtns[i].setText(options[i]);
            answerBtns[i].setEnabled(true);
        }
        startTimer();
    }
    //------------------------------------------
    // Answer Selected

    private void handleAnswer(int idx) {

        selectedAnswer = idx;

        stopTimer();

        for(JButton btn : answerBtns) {
            btn.setEnabled(false);
        }
        new javax.swing.Timer(1000, e -> {
            ((javax.swing.Timer)e.getSource()).stop();
            window.submitAnswer(idx);

        }).start();
    }
    //------------------------------------------
    // Timer
    public void startTimer() {
        stopTimer();

        timeLeft = 20;

        timerLabel.setText("20");
        timerLabel.setForeground(new Color(255,200,0));

        questionTimer = new javax.swing.Timer(1000, e -> {

            timeLeft--;
            timerLabel.setText(String.valueOf(timeLeft));

            if(timeLeft <= 5) {
                timerLabel.setForeground(Color.RED);
            }
            if(timeLeft <= 0) {

                stopTimer();
                selectedAnswer = -1;
                window.submitAnswer(-1);
            }
        });
        questionTimer.start();
    }

    public void stopTimer() {

        if(questionTimer != null && questionTimer.isRunning()) {
            questionTimer.stop();
        }
    }

    //------------------------------------------
    // Getters
    public int getSelectedAnswer() {
        return selectedAnswer;
    }

    public int getTimeLeft() {
        return timeLeft;
    }
    // Button Style
    private JButton createAnswerButton(
            String text,
            Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);

        button.setBorder(
            BorderFactory.createEmptyBorder(
                30,20,30,20
            )
        );
        return button;
    }
}