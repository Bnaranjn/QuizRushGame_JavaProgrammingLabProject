package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * HostSetupPanel: Starting screen. Host can open a lobby or navigate to Join.
 */
public class HostSetupPanel extends JPanel {
    private final MainWindow window;

    public HostSetupPanel(MainWindow window, int pin) {
        this.window = window;
        setLayout(new BorderLayout());
        setBackground(new Color(24, 28, 48));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(24, 28, 48));
        center.setBorder(new EmptyBorder(80, 60, 80, 60));

        // Title
        JLabel title = new JLabel("QuizRush");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Host your own Kahoot-style quiz game");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setForeground(new Color(180, 180, 200));

        // Room PIN display
        JLabel pinTitle = new JLabel("Your Room PIN");
        pinTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        pinTitle.setFont(new Font("Arial", Font.BOLD, 16));
        pinTitle.setForeground(new Color(180, 180, 200));

        JLabel pinLabel = new JLabel(String.valueOf(pin));
        pinLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        pinLabel.setFont(new Font("Arial", Font.BOLD, 48));
        pinLabel.setForeground(new Color(255, 215, 0));

        JLabel pinNote = new JLabel("Share this PIN with players on your local network");
        pinNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        pinNote.setFont(new Font("Arial", Font.PLAIN, 12));
        pinNote.setForeground(new Color(130, 130, 160));

        // Open Lobby button
        JButton openLobbyBtn = styledButton("Open Lobby", new Color(90, 120, 255));
        openLobbyBtn.addActionListener(e -> {
            window.startHosting();
            window.showScreen(MainWindow.SCREEN_LOBBY);
        });

        // Join Game button
        JButton joinBtn = styledButton("Join Game Instead", new Color(50, 55, 80));
        joinBtn.addActionListener(e -> window.showScreen(MainWindow.SCREEN_JOIN));

        center.add(title);
        center.add(Box.createRigidArea(new Dimension(0, 8)));
        center.add(subtitle);
        center.add(Box.createRigidArea(new Dimension(0, 50)));
        center.add(pinTitle);
        center.add(Box.createRigidArea(new Dimension(0, 8)));
        center.add(pinLabel);
        center.add(Box.createRigidArea(new Dimension(0, 6)));
        center.add(pinNote);
        center.add(Box.createRigidArea(new Dimension(0, 50)));
        center.add(openLobbyBtn);
        center.add(Box.createRigidArea(new Dimension(0, 14)));
        center.add(joinBtn);

        add(center, BorderLayout.CENTER);
    }

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
}
