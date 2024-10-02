package src.bricker.gameobjects;

import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import src.bricker.main.BrickerGameManager;

/**
 * Represents a puck object in the game, extending the Ball class with specific behavior and properties.
 */
public class Puck extends Ball {
    /**
     * The tag associated with the Puck object.
     */
    public static final String TAG = "Puck";
    /**
     * The game manager responsible for managing game objects.
     */
    private BrickerGameManager brickerGameManager;


    /**
     * Constructs a new Ball instance.
     *
     * @param topLeftCorner  The top-left corner of the ball in window coordinates (pixels).
     * @param dimensions     The dimensions (width and height) of the ball.
     * @param renderable     The visual representation of the ball.
     * @param collisionSound The sound to play when the ball collides with another object.
     */
    public Puck(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, Sound collisionSound,
                BrickerGameManager manager) {
        super(topLeftCorner, dimensions, renderable, collisionSound);
        setTag(TAG);
        brickerGameManager = manager;
    }

    /**
     * removes it from the game if it goes out of bounds.
     *
     * @param deltaTime Time since the last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        Vector2 windowDimensions = brickerGameManager.getWindowDimantsions();
        Vector2 center = getCenter();
        if ((center.x() < 0 || center.x() > windowDimensions.x()) || (center.y() < 0 ||
                center.y() > windowDimensions.y())) {
            // out-of-bounds
            brickerGameManager.removeObject(this);
        }

    }
}
