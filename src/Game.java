import javax.swing.JFrame;

public class Game {
    public Game() {
    }

    public static void main(String[] args) {
        JFrame obj = new JFrame();
        Gameplay gameplay = new Gameplay();  // The button is added in this class
        obj.setTitle("Brick Buster");
        obj.setBounds(450, 100, 700, 600);
        obj.setResizable(false);
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Use proper constant
        obj.add(gameplay);
        obj.setVisible(true);
    }
}
