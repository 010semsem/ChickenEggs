import javax.swing.*;
import java.awt.*;

public class LevelMenu extends JFrame {

    public LevelMenu() {

        setTitle("Select Level");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel panel = new BackgroundPanel("assets/menuBackground.png");
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton easy = createButton("Easy");
        JButton medium = createButton("Medium");
        JButton hard = createButton("Hard");
        JButton back = createButton("Back");

        panel.add(Box.createVerticalGlue());
        panel.add(easy);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(medium);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(hard);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(back);
        panel.add(Box.createVerticalGlue());

        add(panel);

        easy.addActionListener(e -> {
            Chicken.selectedEggSpeed = 1;
            JOptionPane.showMessageDialog(null, "Easy mode selected");
        });

        medium.addActionListener(e -> {
            Chicken.selectedEggSpeed = 2;
            JOptionPane.showMessageDialog(null, "Medium mode selected");
        });

        hard.addActionListener(e -> {
            Chicken.selectedEggSpeed = 3;
            JOptionPane.showMessageDialog(null, "Hard mode selected");
        });

        back.addActionListener(e -> {
            new GameMenu();
            dispose();
        });

        setVisible(true);
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
}
