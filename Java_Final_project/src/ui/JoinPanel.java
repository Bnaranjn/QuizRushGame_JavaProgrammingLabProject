package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * JoinPanel: Screen where a player enters the host's IP, PIN, and their nickname.
 */
public class JoinPanel extends JPanel {
    private final MainWindow window;

    public JoinPanel(MainWindow window, int roomPin) {
        this.window = window;
        setLayout(new BorderLayout());
        setBackground(new Color(24, 28, 48));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(24, 28, 48));
        center.setBorder(new EmptyBorder(60, 60, 60, 60));

        JLabel title = new JLabel("QuizRush");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Join a game. Enter the host's IP and PIN.");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(180, 180, 200));

        // Host IP field
        JTextField ipField = styledField("Host IP  (e.g. 192.168.1.5)");

        // PIN field
        JTextField pinField = styledField("Room PIN");

        // Nickname field
        JTextField nameField = styledField("Your Nickname");

        // Join button
        JButton joinBtn = new JButton("Join >>");
        joinBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        joinBtn.setFont(new Font("Arial", Font.BOLD, 18));
        joinBtn.setBackground(new Color(90, 120, 255));
        joinBtn.setForeground(Color.WHITE);
        joinBtn.setFocusPainted(false);
        joinBtn.setBorder(new EmptyBorder(12, 40, 12, 40));
        joinBtn.setMaximumSize(new Dimension(260, 52));

        joinBtn.addActionListener(e -> {
            String ip   = ipField.getText().trim();
            String pin  = pinField.getText().trim();
            String name = nameField.getText().trim();

            if (ip.isEmpty()) {
                JOptionPane.showMessageDialog(window, "Please enter the host's IP address.");
                return;
            }
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(window, "Please enter a nickname.");
                return;
            }
            window.joinGame(name, ip);
            window.showScreen(MainWindow.SCREEN_WAITINGROOM);
        });

        JButton backBtn = new JButton("<< Back");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        backBtn.setBackground(new Color(40, 45, 70));
        backBtn.setForeground(new Color(180, 180, 200));
        backBtn.setFocusPainted(false);
        backBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        backBtn.addActionListener(e -> window.showScreen(MainWindow.SCREEN_HOST_SETUP));

        center.add(title);
        center.add(Box.createRigidArea(new Dimension(0, 8)));
        center.add(subtitle);
        center.add(Box.createRigidArea(new Dimension(0, 36)));
        center.add(fieldLabel("Host IP Address"));
        center.add(Box.createRigidArea(new Dimension(0, 6)));
        center.add(ipField);
        center.add(Box.createRigidArea(new Dimension(0, 16)));
        center.add(fieldLabel("Room PIN"));
        center.add(Box.createRigidArea(new Dimension(0, 6)));
        center.add(pinField);
        center.add(Box.createRigidArea(new Dimension(0, 16)));
        center.add(fieldLabel("Nickname"));
        center.add(Box.createRigidArea(new Dimension(0, 6)));
        center.add(nameField);
        center.add(Box.createRigidArea(new Dimension(0, 30)));
        center.add(joinBtn);
        center.add(Box.createRigidArea(new Dimension(0, 12)));
        center.add(backBtn);

        add(center, BorderLayout.CENTER);
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        return label;
    }

    private JTextField styledField(String placeholder) {
        JTextField field = new JTextField();
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
