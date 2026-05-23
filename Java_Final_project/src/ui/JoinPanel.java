package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


public class JoinPanel extends JPanel{
	public JoinPanel() {
		
		setLayout(new BorderLayout());
		setBackground(new Color(24, 28, 48));
		
		// Main content panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(24, 28, 48));
        centerPanel.setBorder(new EmptyBorder(80, 60, 80, 60));

		//title at the top
		JLabel title= new JLabel("QuizRush", SwingConstants.CENTER);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setFont(new Font("Arial", Font.BOLD, 36));
		title.setForeground(Color.WHITE);
		
		//add(title, BorderLayout.NORTH);
		
		//subtitle
		JLabel subtitle= new JLabel("Join a quiz. Enter the PIN from your host");
		subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
	    subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
	    subtitle.setForeground(new Color(200, 200, 200));
	    //space
	    centerPanel.add(title);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(subtitle);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        
		//pin field
		JTextField pinField = new JTextField("Enter the pin");
		 pinField.setMaximumSize(new Dimension(300, 40));
		 pinField.setFont(new Font("Arial", Font.PLAIN, 16));
		 pinField.setBorder(BorderFactory.createCompoundBorder(
	                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
	                BorderFactory.createEmptyBorder(10, 10, 10, 10)
	        ));
		 centerPanel.add(pinField);
		 centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
		 
		 JTextField nameField = new JTextField("Enter your name");
		 nameField.setMaximumSize(new Dimension(300, 40));
		 nameField.setFont(new Font("Arial", Font.PLAIN, 16));
		 nameField.setBorder(BorderFactory.createCompoundBorder(
	                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
	                BorderFactory.createEmptyBorder(10, 10, 10, 10)
	        ));
		 centerPanel.add(nameField);
		 centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
		 
		 
		//button
		JButton joinBtn = new JButton("Join ->");
		joinBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		joinBtn.setFont(new Font("Arial", Font.BOLD, 18));
		joinBtn.setBackground(new Color(90, 120, 255));
		joinBtn.setForeground(Color.WHITE);
		joinBtn.setFocusPainted(false);
		joinBtn.setBorder(new EmptyBorder(12, 30, 12, 30));
		//add(openLobby, BorderLayout.SOUTH);
        centerPanel.add(joinBtn);

        add(centerPanel, BorderLayout.CENTER);
		
	}
}
