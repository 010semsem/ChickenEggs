import javax.swing.*;
import java.awt.*;
class HowToPlay extends JFrame {
    public HowToPlay() {
        setTitle("How to Play");
        setSize(700, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel("How to Play", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 34));
        title.setForeground(Color.BLACK);

        JTextArea text = new JTextArea();
        text.setFont(new Font("Arial", Font.PLAIN, 22));
        text.setForeground(Color.BLACK);
        text.setBackground(Color.WHITE);
        text.setOpaque(true);
        text.setEditable(false);
        text.setFocusable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);

        text.setText(
                "Game Objective:\n" +
                        "Catch as many eggs as possible using the basket, and avoid letting any egg fall.\n" +
                        "Try to beat the High Score before the timer runs out.\n\n" +

                        "Controls:\n" +
                        "Right Arrow → Move the basket to the right.\n" +
                        "Left Arrow → Move the basket to the left.\n" +
                        "P → Pause or resume the game.\n" +
                        "M → Return to the main menu anytime.\n\n" +

                        "Win & Lose Conditions:\n" +
                        "If the timer ends and your score is higher than the High Score → YOU WIN.\n" +
                        "If the timer ends and your score is lower than the High Score → YOU LOSE.\n" +
                        "If your lives reach 0 → YOU LOSE."
        );

        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(255, 215, 0));
        backButton.setFont(new Font("Arial", Font.BOLD, 32));
        backButton.setPreferredSize(new Dimension(300, 60));
        backButton.setMaximumSize(new Dimension(300, 60));

        backButton.addActionListener(e -> {
            dispose();
        });

        panel.add(title, BorderLayout.NORTH);
        panel.add(text, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }
}
