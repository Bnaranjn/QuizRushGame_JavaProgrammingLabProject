package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HostSetupPanel extends JPanel {
    private final MainWindow window;

    public HostSetupPanel(MainWindow window, int port) {
        this.window = window;
        setLayout(new BorderLayout());
        setBackground(new Color(24, 28, 48));

        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setBackground(new Color(24, 28, 48));
        centerContainer.setBorder(new EmptyBorder(80, 60, 80, 60));

        JLabel title = new JLabel("QuizRush Framework");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);

        JLabel desc = new JLabel("Distributed Server-Authoritative Quiz Game");
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        desc.setFont(new Font("Arial", Font.PLAIN, 14));
        desc.setForeground(new Color(160, 170, 200));

        JLabel portLabel = new JLabel("Target Network Room Port: " + port);
        portLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        portLabel.setFont(new Font("Arial", Font.BOLD, 18));
        portLabel.setForeground(new Color(241, 196, 15));

        JButton hostBtn = createStyledButton("Initialize Room Lobby", new Color(40, 167, 69));
        hostBtn.addActionListener(e -> window.startHosting());

        JButton clientRouteBtn = createStyledButton("Join External Room Server", new Color(52, 152, 219));
        clientRouteBtn.addActionListener(e -> window.showScreen(MainWindow.SCREEN_JOIN));

        centerContainer.add(title);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        centerContainer.add(desc);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 40)));
        centerContainer.add(portLabel);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 40)));
        centerContainer.add(hostBtn);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        centerContainer.add(clientRouteBtn);

        add(centerContainer, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(300, 50));
        return btn;
    }
}