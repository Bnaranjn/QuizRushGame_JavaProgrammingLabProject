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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;


public class LobbyPanel extends JPanel {
	private JPanel playerListPanel; 
	public LobbyPanel(int pin) {
		
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
		 
		// Players waiting label
		 JLabel waitingLabel = new JLabel("Players joined:");
		 waitingLabel.setForeground(new Color(180, 180, 180));
		 waitingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		 waitingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		 centerPanel.add(waitingLabel);
		 centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		 //player names
		 playerListPanel = new JPanel();
		 playerListPanel.setLayout(new BoxLayout(playerListPanel, BoxLayout.Y_AXIS));
		 playerListPanel.setBackground(new Color(24, 28, 48));
		 playerListPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		 centerPanel.add(playerListPanel);
		 centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
      		
		//button
			JButton joinBtn = new JButton("Start quiz");
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
	// Member A calls this when a new player joins over the socket
	public void addPlayer(String playerName) {
	    SwingUtilities.invokeLater(() -> {
	        JLabel chip = new JLabel("-" + playerName);
	        chip.setForeground(new Color(100, 220, 150));
	        chip.setFont(new Font("Arial", Font.PLAIN, 16));
	        chip.setAlignmentX(Component.CENTER_ALIGNMENT);
	        playerListPanel.add(chip);
	        playerListPanel.revalidate();
	        playerListPanel.repaint();
	    });
	}
}
