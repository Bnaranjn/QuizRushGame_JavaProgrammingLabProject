package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class QuestionSetPickerPanel extends JPanel {
    private final MainWindow window;
    private String selectedFile = "general.txt";

    public QuestionSetPickerPanel(MainWindow window) {
        this.window = window;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(24, 28, 48));
        setBorder(new EmptyBorder(60, 80, 60, 80));

        JLabel title = new JLabel("Choose Questions");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Pick a set or load your own from a file");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(144, 144, 176));

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(255, 100, 100));

        JPanel generalCard = makeCard("General Knowledge", "general.txt");
        JPanel techCard    = makeCard("Tech & CS Fundamentals", "tech.txt");

        highlightCard(generalCard, true);
        highlightCard(techCard, false);

        generalCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedFile = "general.txt";
                highlightCard(generalCard, true);
                highlightCard(techCard, false);
                statusLabel.setText(" ");
            }
        });
        techCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedFile = "tech.txt";
                highlightCard(generalCard, false);
                highlightCard(techCard, true);
                statusLabel.setText(" ");
            }
        });

        JButton loadFileBtn = styledButton("Load from File", new Color(50, 54, 96));
        loadFileBtn.setForeground(new Color(157, 180, 255));
        loadFileBtn.addActionListener(e -> {
            JTextField field = new JTextField(20);
            field.setFont(new Font("Arial", Font.PLAIN, 14));
            field.setBackground(new Color(34, 39, 74));
            field.setForeground(Color.WHITE);
            field.setCaretColor(Color.WHITE);
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 75, 120)),
                new EmptyBorder(6, 10, 6, 10)
            ));

            JLabel label = new JLabel("Enter the name of your quiz source file (e.g., quiz.txt):");
            label.setFont(new Font("Arial", Font.PLAIN, 13));
            label.setForeground(new Color(200, 200, 220));

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(new Color(24, 28, 48));
            panel.add(label);
            panel.add(Box.createRigidArea(new Dimension(0, 8)));
            panel.add(field);

            UIManager.put("OptionPane.background", new Color(24, 28, 48));
            UIManager.put("Panel.background", new Color(24, 28, 48));
            UIManager.put("OptionPane.messageForeground", Color.WHITE);

            int result = JOptionPane.showConfirmDialog(window, panel,
                "Load Quiz", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String fileName = field.getText().trim();
                if (fileName.isEmpty()) return;
                if (!new File(fileName).exists()) {
                    statusLabel.setText("Couldn't find file: " + fileName);
                    return;
                }
                window.setPendingQuestionFile(fileName);
                window.startHosting(window.getPendingPort());
            }
        });

        JButton openLobbyBtn = styledButton("Open Lobby", new Color(90, 120, 255));
        openLobbyBtn.addActionListener(e -> {
            window.setPendingQuestionFile(selectedFile);
            window.startHosting(window.getPendingPort());
        });

        JButton backBtn = styledButton("Back", new Color(50, 54, 96));
        backBtn.setForeground(new Color(144, 144, 176));
        backBtn.addActionListener(e -> window.showScreen(MainWindow.SCREEN_SETUP));

        add(title);
        add(Box.createRigidArea(new Dimension(0, 6)));
        add(subtitle);
        add(Box.createRigidArea(new Dimension(0, 32)));
        add(generalCard);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(techCard);
        add(Box.createRigidArea(new Dimension(0, 24)));
        add(loadFileBtn);
        add(Box.createRigidArea(new Dimension(0, 8)));
        add(statusLabel);
        add(Box.createRigidArea(new Dimension(0, 24)));
        add(openLobbyBtn);
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(backBtn);
    }

    private JPanel makeCard(String name, String file) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(34, 39, 74));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(58, 61, 92), 1),
            new EmptyBorder(14, 18, 14, 18)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
        nameLabel.setForeground(Color.WHITE);

        JLabel fileLabel = new JLabel(file);
        fileLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        fileLabel.setForeground(new Color(112, 112, 160));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(new Color(34, 39, 74));
        left.add(nameLabel);
        left.add(Box.createRigidArea(new Dimension(0, 3)));
        left.add(fileLabel);

        card.add(left, BorderLayout.CENTER);
        return card;
    }

    private void highlightCard(JPanel card, boolean selected) {
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                selected ? new Color(90, 120, 255) : new Color(58, 61, 92),
                selected ? 2 : 1
            ),
            new EmptyBorder(14, 18, 14, 18)
        ));
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 40, 12, 40));
        btn.setMaximumSize(new Dimension(320, 50));
        return btn;
    }
}