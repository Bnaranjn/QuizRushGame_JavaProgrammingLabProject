package ui;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class QuestionPanel extends JPanel{
	QuestionPanel(){
		setLayout(new BorderLayout());
		setBackground(new Color(18,18,30));
		
		//top panel
		
		JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(28, 28, 45));
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
      
        //!!!!!modification needed for the label 1/10,2/10 etc
        //-----------------------------------------------------
        JLabel questionNumber = new JLabel("Question 1/10");
        questionNumber.setForeground(Color.WHITE);
        questionNumber.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel timerLabel = new JLabel("20");
        timerLabel.setForeground(new Color(255, 200, 0));
        timerLabel.setFont(new Font("Arial", Font.BOLD, 28));

        topPanel.add(questionNumber, BorderLayout.WEST);
        topPanel.add(timerLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        
        //center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(18, 18, 30));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(50, 40, 50, 40));

        JLabel questionText = new JLabel(
                "<html><div style='text-align:center;'>"
                + "Which planet is known as the Red Planet?"
                + "</div></html>"
        );
        questionText.setForeground(Color.WHITE);
        questionText.setFont(new Font("Arial", Font.BOLD, 30));
        questionText.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(questionText);

        add(centerPanel, BorderLayout.CENTER);
        
        //answer buttons
        JPanel answerPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        answerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        answerPanel.setBackground(new Color(18, 18, 30));

        JButton btn1 = createAnswerButton("Earth", new Color(230, 57, 70));
        JButton btn2 = createAnswerButton("Mars", new Color(69, 123, 157));
        JButton btn3 = createAnswerButton("Venus", new Color(255, 183, 3));
        JButton btn4 = createAnswerButton("Jupiter", new Color(76, 175, 80));

        answerPanel.add(btn1);
        answerPanel.add(btn2);
        answerPanel.add(btn3);
        answerPanel.add(btn4);

        add(answerPanel, BorderLayout.SOUTH);
	}
	private JButton createAnswerButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setForeground(Color.WHITE);

        button.setBackground(color);

        button.setFocusPainted(false);

        button.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        return button;
    }
	

}
