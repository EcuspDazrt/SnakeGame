package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JFrame {
        private Snake.Direction currentDirection = Snake.Direction.RIGHT;
        private boolean inputGiven = false;
        private Snake.Direction nextDirection = Snake.Direction.NONE;
        private Snake snake;
        private GameBoard board;
        private Food food;
        private GameBoardPanel boardPanel;
        private CardLayout cardLayout;
        private JPanel mainPanel;
        private MenuPanel menuPanel;
        public static SnakeSpeed snakeSpeed;
        public static BoardSize boardSize;
        private boolean isPaused = false;

        public SnakeGame() {
            cardLayout = new CardLayout();
            mainPanel = new JPanel(cardLayout);
            snakeSpeed = new SnakeSpeed(50);
            boardSize = new BoardSize(20);

            menuPanel = new MenuPanel(this);
            mainPanel.add(menuPanel, "Menu");

            snake = new Snake(boardSize.get() / 2, boardSize.get() / 2);
            board = new GameBoard(boardSize.get());
            food = new Food(board);

            boardPanel = new GameBoardPanel(board);
            mainPanel.add(boardPanel, "Game");

            this.add(mainPanel);
            showMenu();

            gameLoop();

            this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (inputGiven) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            nextDirection = Snake.Direction.UP;
                            break;
                        case KeyEvent.VK_DOWN:
                            nextDirection = Snake.Direction.DOWN;
                            break;
                        case KeyEvent.VK_LEFT:
                            nextDirection = Snake.Direction.LEFT;
                            break;
                        case KeyEvent.VK_RIGHT:
                            nextDirection = Snake.Direction.RIGHT;
                            break;
                        case KeyEvent.VK_ESCAPE:
                            isPaused = !isPaused;
                            break;
                        case KeyEvent.VK_ENTER:
                            if (menuPanel.isVisible()) {startGame();}
                            break;
                        case KeyEvent.VK_R:
                            timer.stop();
                            showMenu();
                            break;
                    }
                } else {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            if (currentDirection != Snake.Direction.DOWN) currentDirection = Snake.Direction.UP;
                            break;
                        case KeyEvent.VK_DOWN:
                            if (currentDirection != Snake.Direction.UP) currentDirection = Snake.Direction.DOWN;
                            break;
                        case KeyEvent.VK_LEFT:
                            if (currentDirection != Snake.Direction.RIGHT) currentDirection = Snake.Direction.LEFT;
                            break;
                        case KeyEvent.VK_RIGHT:
                            if (currentDirection != Snake.Direction.LEFT) currentDirection = Snake.Direction.RIGHT;
                            break;
                        case KeyEvent.VK_ESCAPE:
                            isPaused = !isPaused;
                            break;
                        case KeyEvent.VK_ENTER:
                            if (menuPanel.isVisible()) {startGame();}
                            break;
                        case KeyEvent.VK_R:
                            timer.stop();
                            showMenu();
                            break;
                    }
                    inputGiven = true;
                }

            }
        });

            this.setFocusable(true);
            this.requestFocusInWindow();

        }

        // do frame, panel, timer
        private Timer timer;
        private boolean ateFood = false;

        private void gameLoop() {
            if (timer != null) {
                timer.stop();
                timer = null;
            }

            timer = new Timer(snakeSpeed.get() + 50, e -> {
                if (!inputGiven && nextDirection != Snake.Direction.NONE) {
                    currentDirection = nextDirection;
                    nextDirection = Snake.Direction.NONE;
                    System.out.println(currentDirection);
                }

                if (!isPaused) {
                    snake.move(currentDirection, ateFood, snake.getBody(), board);
                }
                inputGiven = false;

                if (snake.checkCollision(board.cols, board.rows)) {
                    timer.stop();
                    showMenu();
                }

                if (snake.getBody().getFirst().equals(food.getPosition())) {
                    ateFood = true;
                    food.updateFood(board);
                } else {
                    ateFood = false;
                }
                boardPanel.repaint();
            });
        timer.start();
        }

    public void showMenu() {
        cardLayout.show(mainPanel, "Menu");
    }

    public void startGame() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        board = new GameBoard(boardSize.get());
        snake = new Snake(5, 5);
        food = new Food(board);
        ateFood = false;
        currentDirection = Snake.Direction.RIGHT;


        boardPanel.board = board;

        cardLayout.show(mainPanel, "Game");
        gameLoop();
        this.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SnakeGame game = new SnakeGame();

        game.setTitle("src.Snake Game");
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setResizable(false);
        game.pack();
        game.setVisible(true);
        game.setFocusable(true);
    }
}


class GameBoard {
    // initializing the gameboard, can change size here
    int rows;
    int cols;
    int[][] grid;
    public GameBoard(int rows) {
        this.rows = rows;
        this.cols = rows;
        grid = new int[this.rows][this.cols];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                grid[i][j] = 0;
            }
        }
    }
}


class Food  {
    Random random = new Random();
    int[] currentFoodPos = new int[2];
    Point foodPointer = new Point();

    public Food(GameBoard board) {
        currentFoodPos = new int[]{8, 8};
        foodPointer.setLocation(8, 8);
        board.grid[8][8] = 2;
    }

    public void updateFood(GameBoard board) {
    // generating new coordinates
    int yCoord = random.nextInt(board.rows);
    int xCoord = random.nextInt(board.cols);
    int[] nextFoodPos = {xCoord, yCoord};
    // put position into grid, recalculate if same or on snake (haven't done snake logic yet)
    if (board.grid[xCoord][yCoord] != 1) {
        currentFoodPos = nextFoodPos;
        foodPointer.setLocation(currentFoodPos[1], currentFoodPos[0]);
        board.grid[currentFoodPos[0]][currentFoodPos[1]] = 2;
    } else {
        updateFood(board);
    }
    }

    public Point getPosition() {
        return foodPointer;
    }
}


class GameBoardPanel extends JPanel {
public GameBoard board;
private int cellSize;

//private Image snakeSprite;
private Image foodSpriteDark;
private Image foodSpriteLight;

public GameBoardPanel(GameBoard board) {
    this.board = board;
    setPreferredSize(new Dimension(800, 800));

    try {
//        snakeSprite = ImageIO.read(getClass().getResource("/sprites/snake.png"));
        foodSpriteDark = ImageIO.read(getClass().getResource("/sprites/foodDark.png"));
        foodSpriteLight = ImageIO.read(getClass().getResource("/sprites/foodLight.png"));
    } catch (IOException e) {
        e.printStackTrace();
    }
}

     @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            cellSize = 800 / board.cols;
            for (int y = 0; y < board.rows; y++) {
                for (int x = 0; x < board.cols; x++) {
                    Image sprite;
                    Color customGreenLight = new Color(170, 215, 81);
                    Color customGreenDark = new Color(162, 209, 73);
                    Color customBlue = new Color(71, 117, 235);
                    switch(board.grid[y][x]) {
                        case 0:
                            if ((x + y) % 2 == 0) {g.setColor(customGreenLight);
                            } else {g.setColor(customGreenDark); }
                            g.fillRect(x*cellSize, y*cellSize, cellSize, cellSize);
                            break;
                        case 1: g.setColor(customBlue); g.fillRect(x*cellSize, y*cellSize, cellSize, cellSize); break;
                        case 2: if ((x + y) % 2 == 0) { sprite = foodSpriteLight; } else { sprite = foodSpriteDark; }
                                g.drawImage(sprite, x * cellSize, y * cellSize, cellSize, cellSize, this);
                                break;
                    }
                }
            }
        }
}


class MenuPanel extends JPanel {
    public MenuPanel(SnakeGame game) {
        setLayout(new GridBagLayout());
        JButton startButton = new JButton("Start Game");
        JButton exitButton = new JButton("Exit");

        startButton.addActionListener(e -> game.startGame());
        exitButton.addActionListener(e -> System.exit(0));

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        slider.setPaintTicks(false);
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(10);
        slider.setInverted(true);
        slider.setPaintLabels(true);

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0, new JLabel("Fast"));
        labelTable.put(50, new JLabel("Medium"));
        labelTable.put(100, new JLabel("Slow"));
        slider.setLabelTable(labelTable);

        JSlider slider2 = new JSlider(JSlider.HORIZONTAL, 10, 20, 15);
        slider2.setPaintTicks(false);
        slider2.setMajorTickSpacing(5);
        slider2.setMinorTickSpacing(1);
        slider2.setPaintLabels(true);

        Hashtable<Integer, JLabel> labelTable2 = new Hashtable<>();
        labelTable2.put(10, new JLabel("Small"));
        labelTable2.put(15, new JLabel("Medium"));
        labelTable2.put(20, new JLabel("Big"));
        slider2.setLabelTable(labelTable2);

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // fallback to default
        }

        slider.addChangeListener(new  ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int value = source.getValue();
                SnakeGame.snakeSpeed.set(value);
                System.out.println(SnakeGame.snakeSpeed.get());
            }
        });

        slider2.addChangeListener(new  ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int value = source.getValue();
                SnakeGame.boardSize.set(value);
                System.out.println(SnakeGame.boardSize.get());
            }
        });

        add(startButton);
        add(exitButton);
        add(slider);
        add(slider2);
    }
}


class Snake {

        public enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE;

        public Direction opposite() {
            switch(this) {
                case UP: return DOWN;
                case DOWN: return UP;
                case RIGHT: return LEFT;
                case LEFT: return RIGHT;
            }
            throw new IllegalStateException("Unknown direction: " + this);
        }
    } // defining directions

    private LinkedList<Point> body;

    public Snake(int startX, int startY) {
        body = new LinkedList<>(); // creating body
        body.add(new Point(startX, startY)); // initial head
    }

    public void move(Direction dir, boolean ateFood, LinkedList<Point> body, GameBoard board) {
        Point head = body.getFirst();
        Point newHead = new Point(head);

        switch (dir) {
            case UP: newHead.y -= 1; break;
            case DOWN: newHead.y += 1; break;
            case RIGHT: newHead.x += 1; break;
            case LEFT: newHead.x -= 1; break;
        }

        body.addFirst(newHead);


        convertSnakeGridHead(body, board);

        if (!ateFood) {
            convertSnakeGridTail(body, board);
            body.removeLast();
        }
    }

    public boolean checkCollision(int boardWidth, int boardHeight) {
        Point head = body.getFirst();
        // checks wall-collision
        if (head.x < 0 || head.x >= boardWidth || head.y >= boardHeight || head.y < 0) {
            System.out.println("Wall collision detected at: " + head);
            return true;
        }
        // checks self-collision
        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }
        return false;
    }
    // getter:
    public LinkedList<Point> getBody() {
        return body;
    }

    public void convertSnakeGridHead(LinkedList<Point> body, GameBoard board) {

        int headX = body.getFirst().x;
        int headY = body.getFirst().y;
        if (headX < 0 || headX >= board.cols || headY >= board.rows || headY < 0) {
            return;
        }
        
        board.grid[headY][headX] = 1;
    }

    public void convertSnakeGridTail(LinkedList<Point> body, GameBoard board) {
        int lastX = body.getLast().x;
        int lastY = body.getLast().y;

        board.grid[lastY][lastX] = 0;
    }
}


class SnakeSpeed {
    int speed;
    public SnakeSpeed(int speed) {
        this.speed = speed;
    }
    public void set(int num) {
        speed = num;
    }
    public int get() {
        return speed;
    }
}


class BoardSize {
    int boardSize;
    public BoardSize(int boardSize) { this.boardSize = boardSize; }
    public void set(int num) { boardSize = num; }
    public int get() { return boardSize; }
}