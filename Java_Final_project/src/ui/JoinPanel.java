package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class JoinPanel extends JPanel {
    private final MainWindow window;

    // Input fields used to collect connection details from the player
    private final JTextField ipInputField;
    private final JTextField portInputField;
    private final JTextField profileNameField;

    public JoinPanel(MainWindow window) {
        this.window = window;

        setLayout(new BorderLayout());
        setBackground(new Color(24, 28, 48));

        // Main container for all join-game controls
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(new Color(24, 28, 48));
        wrapper.setBorder(new EmptyBorder(60, 60, 60, 60));

        // Screen title
        JLabel title = new JLabel("QuizRush");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Join a game.");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(180, 180, 200));

        wrapper.add(title);
        wrapper.add(Box.createRigidArea(new Dimension(0, 8)));
        wrapper.add(subtitle);
        wrapper.add(Box.createRigidArea(new Dimension(0, 36)));

        // Host IP address input
        wrapper.add(fieldLabel("Host IP Address"));
        wrapper.add(Box.createRigidArea(new Dimension(0, 6)));

        ipInputField = styledField("localhost");
        wrapper.add(ipInputField);

        // Port input
        wrapper.add(Box.createRigidArea(new Dimension(0, 16)));
        wrapper.add(fieldLabel("Target Port"));
        wrapper.add(Box.createRigidArea(new Dimension(0, 6)));

        portInputField = styledField("5000");
        wrapper.add(portInputField);

        // Player nickname input
        wrapper.add(Box.createRigidArea(new Dimension(0, 16)));
        wrapper.add(fieldLabel("Nickname"));
        wrapper.add(Box.createRigidArea(new Dimension(0, 6)));

        profileNameField = styledField("GuestPlayer");
        wrapper.add(profileNameField);

        wrapper.add(Box.createRigidArea(new Dimension(0, 30)));

        // Button used to attempt a connection to the host
        JButton submitConnectionBtn = new JButton("Join");
        submitConnectionBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitConnectionBtn.setFont(new Font("Arial", Font.BOLD, 18));
        submitConnectionBtn.setBackground(new Color(90, 120, 255));
        submitConnectionBtn.setForeground(Color.WHITE);
        submitConnectionBtn.setFocusPainted(false);
        submitConnectionBtn.setBorder(new EmptyBorder(12, 40, 12, 40));
        submitConnectionBtn.setMaximumSize(new Dimension(260, 52));

        submitConnectionBtn.addActionListener(e -> {
            String ip = ipInputField.getText().trim();
            String portStr = portInputField.getText().trim();
            String name = profileNameField.getText().trim();

            // Make sure the user filled in every field
            if (ip.isEmpty() || portStr.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(
                        window,
                        "All fields are mandatory.",
                        "Input Validation Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            try {
                int parsedPort = Integer.parseInt(portStr);

                // Pass connection details to the main window
                window.connectToGameLobby(name, ip, parsedPort);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        window,
                        "Port must be a number.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        wrapper.add(submitConnectionBtn);

        add(wrapper, BorderLayout.CENTER);
    }

    // Creates a consistent label style for form fields
    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        return label;
    }

    // Creates a themed text field matching the application's UI
    private JTextField styledField(String defaultValue) {
        JTextField field = new JTextField(defaultValue);
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