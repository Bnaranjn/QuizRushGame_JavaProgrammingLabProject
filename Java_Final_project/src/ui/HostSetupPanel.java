package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HostSetupPanel extends JPanel{
	private MainWindow window;
	
	public HostSetupPanel(MainWindow window, int pin) {
		this.window=window;
		
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
		JLabel subtitle= new JLabel("Host your own quiz game");
		subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
	    subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
	    subtitle.setForeground(new Color(200, 200, 200));
	    //space
	    centerPanel.add(title);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(subtitle);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        
		//name field
		JTextField nameField = new JTextField("Enter your name");
		 nameField.setMaximumSize(new Dimension(300, 40));
		 nameField.setFont(new Font("Arial", Font.PLAIN, 16));
		 nameField.setBorder(BorderFactory.createCompoundBorder(
	                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
	                BorderFactory.createEmptyBorder(10, 10, 10, 10)
	        ));
		 centerPanel.add(nameField);
		 centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
		 
		//room pin
		 //int pin = 1000 + (int)(Math.random() * 9000);
		//room pin title
		 JLabel roomPinTitle = new JLabel("Room PIN");
		 roomPinTitle.setForeground(Color.WHITE);
		 roomPinTitle.setFont(new Font("Arial", Font.BOLD, 18));
		 roomPinTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Display pin
		 JLabel roomPinLabel = new JLabel(String.valueOf(pin));
		 roomPinLabel.setForeground(new Color(255, 215, 0));
		 roomPinLabel.setFont(new Font("Arial", Font.BOLD, 40));
		 roomPinLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		// Add to panel
		 centerPanel.add(roomPinTitle);
		 centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		 centerPanel.add(roomPinLabel);
		 centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
		 
		//button
		JButton openLobby = new JButton("Open Lobby");
		openLobby.setAlignmentX(Component.CENTER_ALIGNMENT);
        openLobby.setFont(new Font("Arial", Font.BOLD, 18));
        openLobby.setBackground(new Color(90, 120, 255));
        openLobby.setForeground(Color.WHITE);
        openLobby.setFocusPainted(false);
        openLobby.setBorder(new EmptyBorder(12, 30, 12, 30));
		//add(openLobby, BorderLayout.SOUTH);
        centerPanel.add(openLobby);

        add(centerPanel, BorderLayout.CENTER);
        
        openLobby.addActionListener(e->{
        	window.showScreen(MainWindow.SCREEN_LOBBY);
        });
		
	}
}
