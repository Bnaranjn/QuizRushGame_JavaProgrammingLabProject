package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class JoinPanel extends JPanel {
    private final MainWindow window;
    private final JTextField ipInputField;
    private final JTextField portInputField;
    private final JTextField profileNameField;

    public JoinPanel(MainWindow window) {
        this.window = window;
        setLayout(new BorderLayout());
        setBackground(new Color(24, 28, 48));

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(new Color(24, 28, 48));
        wrapper.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel formTitle = new JLabel("Connect to Server Room");
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        formTitle.setFont(new Font("Arial", Font.BOLD, 26));
        formTitle.setForeground(Color.WHITE);
        wrapper.add(formTitle);
        wrapper.add(Box.createRigidArea(new Dimension(0, 30)));

        ipInputField = buildInputRow(wrapper, "Host Target IP Address:", "localhost");
        portInputField = buildInputRow(wrapper, "Target Active Port:", "5000");
        profileNameField = buildInputRow(wrapper, "Player Handle Nickname:", "GuestPlayer");

        wrapper.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton submitConnectionBtn = new JButton("Establish Connection Link");
        submitConnectionBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitConnectionBtn.setFont(new Font("Arial", Font.BOLD, 16));
        submitConnectionBtn.setBackground(new Color(155, 89, 182));
        submitConnectionBtn.setForeground(Color.WHITE);
        submitConnectionBtn.setFocusPainted(false);
        submitConnectionBtn.setMaximumSize(new Dimension(280, 45));
        
        submitConnectionBtn.addActionListener(e -> {
            String ip = ipInputField.getText().trim();
            String portStr = portInputField.getText().trim();
            String profileName = profileNameField.getText().trim();

            if (ip.isEmpty() || portStr.isEmpty() || profileName.isEmpty()) {
                JOptionPane.showMessageDialog(window, "All credential target parameter entries are mandatory.", "Input Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int parsedPort = Integer.parseInt(portStr);
                window.connectToGameLobby(profileName, ip, parsedPort);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(window, "Port configuration entry must be numeric values.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        wrapper.add(submitConnectionBtn);
        add(wrapper, BorderLayout.CENTER);
    }

    private JTextField buildInputRow(JPanel target, String description, String defaults) {
        JLabel lbl = new JLabel(description);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setForeground(Color.LIGHT_GRAY);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        target.add(lbl);
        target.add(Box.createRigidArea(new Dimension(0, 4)));

        JTextField field = new JTextField(defaults);
        field.setMaximumSize(new Dimension(280, 35));
        field.setFont(new Font("Monospaced", Font.PLAIN, 14));
        field.setHorizontalAlignment(JTextField.CENTER);
        target.add(field);
        target.add(Box.createRigidArea(new Dimension(0, 15)));
        return field;
    }
}