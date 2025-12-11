import javax.swing.*;
import java.awt.*;

public class GameMenu extends JFrame {

    public GameMenu() {
        setTitle("Chicken Game Menu");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        BackgroundPanel mainPanel = new BackgroundPanel("assets/menuBackground.png");
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JButton playButton = createButton("Play");
        JButton levelsButton = createButton("Levels");
        JButton howToPlayButton = createButton("How to Play");
        JButton exitButton = createButton("Exit");

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(playButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(levelsButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(howToPlayButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(exitButton);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
        setVisible(true);

        playButton.addActionListener(e -> {
            new test();
            dispose();
        });
        levelsButton.addActionListener(e -> {
            new LevelMenu();
            dispose();
        });
        howToPlayButton.addActionListener(e -> {
            new HowToPlay();
        });
        exitButton.addActionListener(e -> {
            System.exit(0);
        });
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.BOLD, 32));
        button.setPreferredSize(new Dimension(300, 60));
        button.setMaximumSize(new Dimension(300, 60));
        button.setBackground(new Color(255, 215, 0));
        return button;
    }

    public static void main(String[] args) {
        new GameMenu();
    }
}

class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String fileName) {
        backgroundImage = new ImageIcon(fileName).getImage();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
