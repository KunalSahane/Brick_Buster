import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Gameplay extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;
    private int totalBricks;
    private Timer timer;
    private int delay = 6;

    private int playerX = 310;
    private int ballPosX = 120;
    private int ballPosY = 350;
    private int ballDirX = -2;
    private int ballDirY = -3;

    private int level = 1;

    private MapGenerator map;
    private boolean gameOver = false;

    public Gameplay() {
        play = false;

        startNewLevel();  // Initialize the game with the first level

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        timer = new Timer(delay, this);
        timer.start();
    }

    public void startNewLevel() {
        // Reset the bricks layout and count
        int rows = 5 + level;
        int cols = 10;
        map = new MapGenerator(rows, cols);
        totalBricks = rows * cols;

        // Reset ball position and direction
        ballPosX = 120;
        ballPosY = 350 + rows * map.brickHeight;
        ballDirX = -2;
        ballDirY = -3;

        // Reset paddle position
        playerX = 310;

        // Ensure game state is ready
        play = false;
        gameOver = false;
    }

    public void paint(Graphics g) {
        // Background
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        // Drawing the map (bricks)
        map.draw((Graphics2D) g);

        // Borders
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        // Scores and Level
        g.setColor(Color.white);
        g.setFont(new Font("Serif", Font.BOLD, 25));
        g.drawString("Score: " + score, 500, 30);
        g.drawString("Level: " + level, 50, 30);

        // The paddle
        g.setColor(Color.green);
        g.fillRect(playerX, 550, 100, 8);

        // The ball
        g.setColor(Color.yellow);
        g.fillOval(ballPosX, ballPosY, 20, 20);

        // Message to start the game only when it's not started and not game over
        if (!play && !gameOver) {
            g.setColor(Color.white);
            g.setFont(new Font("Serif", Font.BOLD, 30));
            g.drawString("Press Left or Right key", 180, 300);
            g.drawString("to Start the Game", 220, 340);
        }

        // Game Over condition
        if (ballPosY > 570) {
            play = false;
            ballDirX = 0;
            ballDirY = 0;
            gameOver = true;  // Set game over state to true

            g.setColor(Color.red);
            g.setFont(new Font("Serif", Font.BOLD, 30));
            g.drawString("Game Over, Score: " + score, 190, 300);

            g.setFont(new Font("Serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
        }

        // Level Complete condition
        if (totalBricks <= 0 && !gameOver) {
            play = false;
            ballDirX = 0;
            ballDirY = 0;
            g.setColor(Color.green);
            g.setFont(new Font("Serif", Font.BOLD, 30));
            g.drawString("Level " + level + " Complete!", 210, 300);

            g.setFont(new Font("Serif", Font.BOLD, 20));
            g.drawString("Press Enter to Start Next Level", 200, 350);
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (play) {
            // Paddle and ball interaction
            if (new Rectangle(ballPosX, ballPosY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballDirY = -ballDirY;
            }

            // Brick collision logic
            A: for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);
                        Rectangle brickRect = rect;

                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;

                            if (ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width) {
                                ballDirX = -ballDirX;
                            } else {
                                ballDirY = -ballDirY;
                            }

                            break A;
                        }
                    }
                }
            }

            // Ball movement
            ballPosX += ballDirX;
            ballPosY += ballDirY;

            // Ball boundary check
            if (ballPosX < 0) {
                ballDirX = -ballDirX;
            }
            if (ballPosY < 0) {
                ballDirY = -ballDirY;
            }
            if (ballPosX > 670) {
                ballDirX = -ballDirX;
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600) {
                playerX = 600;
            } else {
                moveRight();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) {
                playerX = 10;
            } else {
                moveLeft();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER && gameOver) {  // Restart only on Enter after game over
            score = 0;  // Reset score to 0
            level = 1;  // Reset level to 1
            startNewLevel();  // Reset game state to the initial level

            repaint();
        }

        // Start the game when the left or right key is pressed, but only if it's not game over
        if (!play && !gameOver && (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)) {
            play = true;
            repaint();
        }
    }

    public void moveRight() {
        play = true;
        playerX += 30;
    }

    public void moveLeft() {
        play = true;
        playerX -= 30;
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}
