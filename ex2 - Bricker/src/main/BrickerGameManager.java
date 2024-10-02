package src.bricker.main;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.ImageRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import danogl.gui.rendering.RectangleRenderable;

import src.bricker.brick_strategies.*;
import src.bricker.gameobjects.*;
import src.bricker.lives_display.DisplayStatus;
import src.bricker.lives_display.GraphicDisplay;
import src.bricker.lives_display.LivesDisplay;
import src.bricker.lives_display.NumericalDisplay;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.HashSet;

/**
 * The BrickerGameManager class extends the GameManager class and is responsible for initializing
 * and managing the various components of the Bricker game, such as the ball, paddles, walls, background,
 * and bricks.
 */
public class BrickerGameManager extends GameManager {
    /**
     * The height of the display for lives.
     */
    public static final int LIVES_DISPLAY_HEIGHT = 30;

    /**
     * The height of each brick in the game.
     */
    private static final int BRICK_HEIGHT = 15;

    /**
     * The color used for the game's border.
     */
    private static final Color BORDER_COLOR = Color.cyan;

    /**
     * The prompt displayed when the player wins the game.
     */
    private static final String WIN_PROMPT = "You Win!";

    /**
     * The prompt displayed when the player loses all lives.
     */
    private static final String LOST_PROMPT = "You lose!";

    /**
     * The prompt encouraging the player to play again.
     */
    private static final String PLAY_AGAIN = " Play again?";

    /**
     * The width of the walls in the game.
     */
    private static final int WALL_WIDTH = 10;

    /**
     * The space between bricks in the game.
     */
    private static final int BRICKS_SPACE = 5;

    /**
     * The vertical offset for the placement of bricks.
     */
    private static final int BRICKER_VERTICAL_OFFSET = WALL_WIDTH + BRICKS_SPACE;

    /**
     * The width of the paddle controlled by the player.
     */
    public static final int PADDLE_WIDTH = 200;

    /**
     * The height of the paddle controlled by the player.
     */
    public static final int PADDLE_HEIGHT = 20;

    /**
     * The vertical position of the paddle relative to the bottom of the game window.
     */
    private static final int VERTICAL_POSSITION_PADDLE = PADDLE_HEIGHT / 2;

    /**
     * The radius of the ball used in the game.
     */
    private static final int BALL_RADUIS = 30;

    /**
     * The radius of the puck, scaled relative to the ball's radius.
     */
    public static final float PUCK_RADUIS = BALL_RADUIS * 0.75f;

    /**
     * The initial number of lives the player starts with.
     */
    private static final int INITIAL_LIVES_NUMBER = 3;

    /**
     * The maximum number of hearts (lives) the player can have.
     */
    public static final int MAX_HEARTS = 4;

    /**
     * The space between hearts in the graphical lives display.
     */
    public static final int SPACE_BETWEEN_HEARTS = 5;

    /**
     * The current number of lives the player has.
     */
    private int currentLives;

    /**
     * The ball object in the game.
     */
    private Ball ball;

    /**
     * The dimensions of the game window.
     */
    private Vector2 windowDimantsions;

    /**
     * The controller for managing the game window.
     */
    private WindowController windowController;

    /**
     * The number of lines of bricks in the game.
     */
    private int linesOfBricks;

    /**
     * The number of bricks per line in the game.
     */
    private int bricksPerLine;

    /**
     * The current number of bricks in the game.
     */
    private int currentBricks;

    /**
     * The display showing the number of lives numerically.
     */
    private NumericalDisplay numerical_lives;

    /**
     * An array of lives displays, supporting multiple types of display (e.g., numerical, graphical).
     */
    private LivesDisplay[] livesDisplays;

    /**
     * A history of lives displays objects for tracking changes.
     */
    private GameObject[][] livesDisplaysHistory;

    /**
     * The display showing the number of lives graphically.
     */
    private GraphicDisplay graphic_lives;

    /**
     * The top-left corner positions of the lives displays.
     */
    private Vector2[] displaysTopLeftCorner;

    /**
     * Flag indicating whether there are no more bricks left in the game.
     */
    private boolean no_more_bricks;

    /**
     * The paddle controlled by the player.
     */
    private Paddle paddle;

    /**
     * A set containing the tags of all bricks in the game.
     */
    private HashSet<String> brickTags;
    private UserInputListener inputListener;

    /**
     * Main entry point of the application.
     *
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        BrickerGameManager manager = new BrickerGameManager(
                "Bricker", new Vector2(700, 500)
        );
        CLIArguments arguments = new CLIArguments(args);
        if (!arguments.validate()) {
            return;
        }

        manager.setBricksPerLine(arguments.getBricksPerLine());
        manager.setLinesOfBricks(arguments.getNumberOfLines());
        manager.run();
    }


    /**
     * constructor function to initialize the game manager
     */
    public BrickerGameManager() {
    }

    /**
     * Constructor that initializes a new instance of BrickerGameManager with a window title and dimensions.
     *
     * @param windowTitle      The title of the game window.
     * @param windowDimensions The dimensions of the game window.
     */
    public BrickerGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
    }

    /**
     * Sets the number of bricks per line.
     *
     * @param bricksPerLine The desired number of bricks per line.
     */
    public void setBricksPerLine(int bricksPerLine) {
        this.bricksPerLine = bricksPerLine;
    }

    /**
     * Sets the number of lines of bricks.
     *
     * @param linesOfBricks The desired number of lines of bricks.
     */
    public void setLinesOfBricks(int linesOfBricks) {
        this.linesOfBricks = linesOfBricks;
    }

    /**
     * Initializes the game by creating the ball, paddles, walls, background, and bricks.
     * It also sets up the window controller and input listeners.
     *
     * @param imageReader      The image reader to load images.
     * @param soundReader      The sound reader to load audio.
     * @param inputListener    The input listener for user interactions.
     * @param windowController The controller for managing the game window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        this.inputListener = inputListener;
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.brickTags = new HashSet<String>();
        currentLives = INITIAL_LIVES_NUMBER;
        this.windowController = windowController;
        windowDimantsions = windowController.getWindowDimensions();

        createBall(imageReader, soundReader);
        createPaddles(imageReader, inputListener);
        createWalls();
        createBackground(imageReader);
        createBricks(imageReader, soundReader, inputListener);
        createLivesDisplays(imageReader);
        no_more_bricks = false;
    }

    /**
     * Called regularly to update the game state, such as checking for game over conditions.
     *
     * @param deltaTime The time elapsed since the last update.
     */
    public void update(float deltaTime) {
        super.update(deltaTime);
        isGameOver();
    }

    /**
     * Removes the given object from the game.
     *
     * @param objectToRemove The object to remove.
     */
    public void removeObject(GameObject objectToRemove) {
        removeObject(objectToRemove, Layer.DEFAULT);
    }

    /**
     * Adds an object to the game at a specified layer.
     *
     * @param objectToAdd The GameObject to add.
     * @param layerID     The layer ID where the object should be added.
     */
    public void addObject(GameObject objectToAdd, int layerID) {
        gameObjects().addGameObject(objectToAdd, layerID);
    }

    /**
     * Adds an object to the game at the default layer.
     *
     * @param objectToAdd The GameObject to add.
     */
    public void addObject(GameObject objectToAdd) {
        addObject(objectToAdd, Layer.DEFAULT);
    }

    /**
     * Removes a specified object from the game at a given layer.
     *
     * @param objectToRemove The object to remove.
     * @param layerID        The layer from which the object should be removed.
     */
    public void removeObject(GameObject objectToRemove, int layerID) {
        gameObjects().removeGameObject(objectToRemove, layerID);
        if (objectToRemove instanceof Brick && brickTags.contains(((Brick) objectToRemove).getTag())) {
            currentBricks--;
            brickTags.remove(((Brick) objectToRemove).getTag());
            if (currentBricks == 0) {
                no_more_bricks = true;
            }
        }
    }

    /**
     * Returns the dimensions of the game window.
     *
     * @return The current dimensions of the game window as a Vector2.
     */
    public Vector2 getWindowDimantsions() {
        return windowDimantsions;
    }

    /**
     * Adds one life to the current count, up to a maximum defined by MAX_HEARTS, and updates the lives
     * display.
     */
    public void addLife() {
        if (currentLives + 1 <= MAX_HEARTS) {
            currentLives++;
            notifyAllLivesDisplays();
        }
    }

    /**
     * Checks if the game is over and if so, prompts the user to play again.
     */
    private void isGameOver() {
        String prompt = "";


        //we won (end all the bricks)
        if (no_more_bricks) {
            prompt = WIN_PROMPT;
        }

        if (ball.getCenter().y() > windowDimantsions.y()) {
            // decrease the lives counter
            currentLives--;
            repositionBall(ball);
            notifyAllLivesDisplays();
        }

        if (currentLives == 0 && prompt.isEmpty()) {
            //we lost
            prompt = LOST_PROMPT;
        }

        if (inputListener.isKeyPressed(KeyEvent.VK_W)) {
            /*
             * user forced win condition by pressing 'W' key.
             */
            prompt = WIN_PROMPT;
        }

        if (!prompt.isEmpty()) {
            // game over
            prompt += PLAY_AGAIN;
            if (windowController.openYesNoDialog(prompt)) {
                ExtraPaddleCollisionStrategy.reset();
                // game to update the static isDisplayed member.
                windowController.resetGame();
            } else {
                windowController.closeWindow();
            }
        }
    }

    /**
     * Repositions the ball to the center of the game window.
     *
     * @param ball The ball object to reposition.
     */
    private void repositionBall(Ball ball) {
        ball.setCenter(windowDimantsions.mult(0.5f));
    }

    /**
     * Creates the ball and adds it to the game.
     *
     * @param imageReader The image reader to use.
     * @param soundReader The sound reader to use.
     */
    private void createBall(ImageReader imageReader, SoundReader soundReader) {
        Renderable ballImage =
                imageReader.readImage("assets/ball.png", true);
        Sound collisionSound = soundReader.readSound("assets/blop_cut_silenced.wav");
        ball = new Ball(Vector2.ZERO, new Vector2(BALL_RADUIS, BALL_RADUIS), ballImage, collisionSound);
        repositionBall(ball);
        gameObjects().addGameObject(ball);
    }

    /**
     * Creates the paddles and adds them to the game.
     *
     * @param imageReader   The image reader to use.
     * @param inputListener The input listener to use.
     */
    private void createPaddles(ImageReader imageReader, UserInputListener inputListener) {
        Renderable paddleImage =
                imageReader.readImage("assets/paddle.png", true);

        createUserPaddle(paddleImage, inputListener);
    }

    /**
     * Creates the user paddle and adds it to the game.
     *
     * @param paddleImage   The image to use for the paddle.
     * @param inputListener The input listener to use from the user.
     */
    private void createUserPaddle(Renderable paddleImage, UserInputListener inputListener) {
        paddle = new Paddle(Vector2.ZERO,
                new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT),
                paddleImage,
                inputListener,
                windowDimantsions);
        paddle.setCenter(new Vector2(windowDimantsions.x() / 2,
                (int) windowDimantsions.y() - VERTICAL_POSSITION_PADDLE));
        gameObjects().addGameObject(paddle);
    }

    /**
     * Creates the AI paddle and adds it to the game.
     *
     * @param paddleImage The image to use for the paddle.
     */
    private void createAIPaddle(Renderable paddleImage) {
        AIPaddle AIPaddle = new AIPaddle(Vector2.ZERO,
                new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT),
                paddleImage,
                ball,
                windowDimantsions);
        AIPaddle.setCenter(new Vector2(windowDimantsions.x() / 2, VERTICAL_POSSITION_PADDLE));
        gameObjects().addGameObject(AIPaddle);
    }

    /**
     * Creates the walls and adds them to the game.
     */
    private void createWalls() {
        RectangleRenderable border = new RectangleRenderable(BORDER_COLOR);
        Vector2[][] walls = new Vector2[][]{
                {new Vector2(0, 0), new Vector2(windowDimantsions.x(), 10)}, // top border
                {new Vector2(0, 0), new Vector2(WALL_WIDTH, windowDimantsions.y())},  // left border
                {new Vector2(windowDimantsions.x() - WALL_WIDTH, 0),
                        new Vector2(WALL_WIDTH, windowDimantsions.y())}   // right border
        };

        for (int i = 0; i < walls.length; i++) {
            GameObject wall = new GameObject(walls[i][0], walls[i][1], border);
            gameObjects().addGameObject(wall);
        }
    }

    /**
     * Creates the background and adds it to the game.
     *
     * @param imageReader The image reader to use.
     */
    private void createBackground(ImageReader imageReader) {
        Renderable backgroundImage =
                imageReader.readImage("assets/DARK_BG2_small.jpeg", false);

        GameObject background = new GameObject(Vector2.ZERO, windowDimantsions, backgroundImage);
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(background, Layer.BACKGROUND);
    }

    /**
     * Creates the bricks and adds them to the game.
     *
     * @param imageReader The image reader to use.
     */
    private void createBricks(ImageReader imageReader, SoundReader soundReader,
                              UserInputListener inputListener) {
        currentBricks = linesOfBricks * bricksPerLine;
        Renderable brickImage =
                imageReader.readImage("assets/brick.png", false);

        Vector2 dims = new Vector2(calculateBrickWidth(), BRICK_HEIGHT);

        CollisionStrategyFactory collisionStrategyFactory = new CollisionStrategyFactory();

        int id = 0;
        for (int row = 0; row < linesOfBricks; row++) {
            for (int col = 0; col < bricksPerLine; col++) {
                CollisionStrategy collisionStrategy = collisionStrategyFactory.build(this,
                        imageReader, soundReader, inputListener, ball);
                Brick brick = new Brick(Vector2.ZERO, dims, brickImage, collisionStrategy);
                brick.setTag("Brick #" + Integer.toString(id++));
                brickTags.add(brick.getTag());
                brick.setTopLeftCorner(new Vector2(WALL_WIDTH + col * (dims.x() + BRICKS_SPACE),
                        BRICKER_VERTICAL_OFFSET + row * (BRICK_HEIGHT + BRICKS_SPACE)));
                gameObjects().addGameObject(brick, Layer.STATIC_OBJECTS);
            }
        }
    }

    /**
     * Calculates the width of each brick based on the game window size and the configured number of bricks
     * per line.
     *
     * @return The calculated width for each brick.
     */
    private float calculateBrickWidth() {
        return ((windowDimantsions.x() - 2 * WALL_WIDTH) - (BRICKS_SPACE * (bricksPerLine - 1))) /
                bricksPerLine;
    }

    /**
     * Creates and initializes the displays for showing lives left in the game. This includes both a numerical
     * display and a graphical display using heart icons. The displays are positioned at the bottom
     * of the game window.
     *
     * @param imageReader The image reader used to load graphical assets for the displays.
     */
    private void createLivesDisplays(ImageReader imageReader) {
        String[] tagsToAvoid = new String[]{
                ball.getTag(), Puck.TAG, paddle.getTag()
        };
        numerical_lives = new NumericalDisplay(Vector2.ZERO,
                new Vector2(LIVES_DISPLAY_HEIGHT, LIVES_DISPLAY_HEIGHT),
                tagsToAvoid);
        ImageRenderable heartImage =
                imageReader.readImage("assets/heart.png", true);
        graphic_lives = new GraphicDisplay(MAX_HEARTS, new Vector2(LIVES_DISPLAY_HEIGHT,
                LIVES_DISPLAY_HEIGHT), SPACE_BETWEEN_HEARTS, heartImage,
                tagsToAvoid);
        livesDisplays = new LivesDisplay[]{
                numerical_lives,
                graphic_lives
        };
        displaysTopLeftCorner = new Vector2[]{
                new Vector2(0,
                        windowDimantsions.y() - LIVES_DISPLAY_HEIGHT),
                new Vector2(40,
                        windowDimantsions.y() - LIVES_DISPLAY_HEIGHT)
        };

        notifyAllLivesDisplays();
    }

    /**
     * update all the displays about a change in the amount of lives.
     *
     * @note hearts objects of GraphicDisplay must be removed first since
     * GraphicDisplay only exports which hearts it holds and can't
     * remove redundant heart objects from the window (GameManager responsibility).
     */
    private void notifyAllLivesDisplays() {
        for (int i = 0; i < livesDisplays.length; i++) {
            livesDisplays[i].notify(currentLives);
            livesDisplays[i].setTopLeftCorner(displaysTopLeftCorner[i]);
            GameObject[] objects = livesDisplays[i].getGameObjects();
            DisplayStatus[] toRemove = livesDisplays[i].getObjectsStatus();

            for (int j = 0; j < toRemove.length; j++) {
                switch (toRemove[j]) {
                    case DISPLAYED:
                        break;
                    case REMOVE:
                        gameObjects().removeGameObject(objects[j]);
                        break;
                    case ADD:
                        gameObjects().addGameObject(objects[j]);
                        break;
                    case NONE:
                        break;
                }
            }
        }
    }
}
