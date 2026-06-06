package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.io.File;

public class LobbyPanel extends JPanel {
    private final MainWindow window;
    private final DefaultListModel<String> listModel;
    private final JButton startBtn;
    private final JLabel lobbyCodeLabel;
    private final JLabel countLabel; // CHANGED: added player count label
    

    //CHANGED: colour palette
    private static final Color BG         = new Color(24, 28, 48);
    private static final Color TEXT_COLOR = Color.WHITE;

    public LobbyPanel(MainWindow window) {
        this.window = window;
        this.listModel = new DefaultListModel<>();

        //CHANGED: switched from BorderLayout to BoxLayout Y_AXIS
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG);
        setBorder(new EmptyBorder(50, 60, 50, 60)); 

        //CHANGED: title style
        JLabel titleLabel = new JLabel("QuizRush - Lobby");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_COLOR);
        JLabel portCaption = new JLabel("Room Port");
        portCaption.setAlignmentX(Component.CENTER_ALIGNMENT);
        portCaption.setFont(new Font("Arial", Font.BOLD, 15));
        portCaption.setForeground(new Color(180, 180, 200));

        //CHANGED: port number now large yellow 44pt
        lobbyCodeLabel = new JLabel("----");
        lobbyCodeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        lobbyCodeLabel.setFont(new Font("Arial", Font.BOLD, 44));
        lobbyCodeLabel.setForeground(new Color(255, 215, 0));

        //CHANGED: player count label
        countLabel = new JLabel("0 player(s) joined");
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        countLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        countLabel.setForeground(new Color(150, 200, 150));

        JPanel playerListPanel = new JPanel();
        playerListPanel.setLayout(new BoxLayout(playerListPanel, BoxLayout.Y_AXIS));
        playerListPanel.setBackground(BG);
        playerListPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JList<String> hiddenList = new JList<>(listModel);

        JScrollPane scroll = new JScrollPane(playerListPanel);
        scroll.setBackground(BG);
        scroll.setBorder(BorderFactory.createEmptyBorder()); 
        scroll.setPreferredSize(new Dimension(360, 160));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        scroll.getViewport().setBackground(BG);

        
        startBtn = new JButton("Start Quiz");
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.setFont(new Font("Arial", Font.BOLD, 18));
        startBtn.setBackground(new Color(90, 120, 255)); 
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setBorder(new EmptyBorder(12, 40, 12, 40)); 
        startBtn.setMaximumSize(new Dimension(260, 52));
        startBtn.addActionListener(e -> window.hostLoadAndStartQuiz(window.getPendingQuestionFile()));
        
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(portCaption);
        add(Box.createRigidArea(new Dimension(0, 4)));
        add(lobbyCodeLabel);
        add(Box.createRigidArea(new Dimension(0, 6)));
        add(countLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(scroll);
        add(Box.createRigidArea(new Dimension(0, 24)));
        add(startBtn);

        //store chip panel ref for updatePlayerList
        putClientProperty("chipPanel", playerListPanel);
    }

    public void setRoomPortDisplay(int activePort) {
        
        SwingUtilities.invokeLater(() -> lobbyCodeLabel.setText(String.valueOf(activePort)));
    }

    public void updatePlayerList(List<String> players) {
        SwingUtilities.invokeLater(() -> {
            
            listModel.clear();
            for (String p : players) listModel.addElement(p);

            
            JPanel chipPanel = (JPanel) getClientProperty("chipPanel");
            chipPanel.removeAll();
            Color[] chipColors = {
                new Color(100, 220, 150),
                new Color(100, 180, 255),
                new Color(255, 180, 100),
                new Color(220, 120, 200),
                new Color(120, 220, 220)
            };
            int ci = 0;
            for (String name : players) {
                JLabel chip = new JLabel(">> " + name);
                chip.setForeground(chipColors[ci % chipColors.length]);
                chip.setFont(new Font("Arial", Font.BOLD, 16));
                chip.setAlignmentX(Component.CENTER_ALIGNMENT);
                chipPanel.add(chip);
                chipPanel.add(Box.createRigidArea(new Dimension(0, 6)));
                ci++;
            }
            
            countLabel.setText((players.size()-1) + " player(s) joined");
            chipPanel.revalidate();
            chipPanel.repaint();
        });
    }

    public void setHostControls(boolean isHost) {
        SwingUtilities.invokeLater(() -> startBtn.setVisible(isHost));
    }
}