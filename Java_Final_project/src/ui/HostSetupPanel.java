package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HostSetupPanel extends JPanel {
    private final MainWindow window;

    public HostSetupPanel(MainWindow window, int defaultPort) {
        this.window = window;

        setLayout(new BorderLayout());
        setBackground(new Color(24, 28, 48));

        // Main container that holds all UI elements vertically
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setBackground(new Color(24, 28, 48));
        centerContainer.setBorder(new EmptyBorder(60, 60, 60, 60));

        // Game title
        JLabel title = new JLabel("QuizRush");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 45));
        title.setForeground(Color.WHITE);

        // Short description shown under the title
        JLabel desc = new JLabel("Host your own quiz game");
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        desc.setFont(new Font("Arial", Font.PLAIN, 22));
        desc.setForeground(new Color(160, 170, 200));

        // Panel containing the custom port configuration controls
        JPanel portConfigPanel = new JPanel();
        portConfigPanel.setLayout(new BoxLayout(portConfigPanel, BoxLayout.Y_AXIS));
        portConfigPanel.setBackground(new Color(24, 28, 48));
        portConfigPanel.setMaximumSize(new Dimension(260, 65));

        JLabel portLabel = new JLabel("Set Custom Server Lobby Port:");
        portLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        portLabel.setFont(new Font("Arial", Font.BOLD, 16));
        portLabel.setForeground(new Color(180, 180, 200));

        portConfigPanel.add(portLabel);
        portConfigPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Pre-fill the field with the default server port
        JTextField portInputField = styledField(String.valueOf(defaultPort));
        portConfigPanel.add(portInputField);

        // Starts the hosting flow after validating the port number
        JButton hostBtn = styledButton("Create Room", new Color(90, 120, 255));
        hostBtn.addActionListener(e -> {
            try {
                int selectedPort = Integer.parseInt(portInputField.getText().trim());

                if (selectedPort < 1024 || selectedPort > 65535) {
                    JOptionPane.showMessageDialog(
                            window,
                            "Please enter a valid custom port number between 1024 and 65535.",
                            "Port Bounds Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                window.setPendingPort(selectedPort);
                window.showScreen(MainWindow.SCREEN_QUESTION_PICKER);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        window,
                        "Port assignments must be plain numerical values.",
                        "Formatting Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Redirects the user to the join-game screen
        JButton clientRouteBtn = styledButton("Join Game Instead", new Color(50, 55, 80));
        clientRouteBtn.addActionListener(e -> window.showScreen(MainWindow.SCREEN_JOIN));

        centerContainer.add(title);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        centerContainer.add(desc);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 45)));
        centerContainer.add(portConfigPanel);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 35)));
        centerContainer.add(hostBtn);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        centerContainer.add(clientRouteBtn);

        add(centerContainer, BorderLayout.CENTER);
    }

    // Creates a consistently styled button used throughout the screen
    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 40, 12, 40));
        btn.setMaximumSize(new Dimension(280, 50));
        return btn;
    }

    // Creates a styled text field matching the application's theme
    private JTextField styledField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setMaximumSize(new Dimension(320, 42));
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setBackground(new Color(36, 41, 66));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 90, 140), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }
}