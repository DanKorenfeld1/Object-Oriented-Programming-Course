package src.bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * Represents a user-controlled paddle in the game. This paddle can be moved horizontally
 * by the user using keyboard inputs. The paddle's movement is constrained within the window boundaries.
 */
public class Paddle extends GameObject implements Collidable {
    /**
     * The tag associated with the paddle object.
     */
    public static final String TAG = "UserPaddle";

    /**
     * The movement speed of the paddle.
     */
    private static final float MOVEMENT_SPEED = 300f;

    /**
     * The number of collisions the paddle has encountered.
     */
    private int collitions;

    /**
     * The listener for user inputs to control the paddle.
     */
    private UserInputListener inputListener;

    /**
     * The dimensions of the paddle.
     */
    private Vector2 dimensions;

    /**
     * The dimensions of the game window, used for boundary checks.
     */
    private Vector2 windowDimensions;

    /**
     * Constructs a new UserPaddle instance.
     *
     * @param topLeftCorner    Position of the paddle, in window coordinates (pixels).
     * @param dimensions       Width and height of the paddle, in window coordinates.
     * @param renderable       The visual representation of the paddle. Can be null,
     *                         in which case the GameObject will not be rendered.
     * @param inputListener    The listener for user inputs to control the paddle.
     * @param windowDimensions The dimensions of the game window, used for boundary checks.
     */
    public Paddle(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, Vector2 windowDimensions) {
        super(topLeftCorner, dimensions, renderable);
        this.dimensions = dimensions;
        this.inputListener = inputListener;
        this.windowDimensions = windowDimensions;
        collitions = 0;
        setTag(TAG);
    }

    /**
     * Updates the paddle's position based on user input. Ensures that the paddle
     * does not move outside the game window's horizontal boundaries.
     *
     * @param deltaTime The time elapsed, in seconds, since the last frame. This is used
     *                  to update the paddle's position based on its velocity.
     */
    public void update(float deltaTime) {
        super.update(deltaTime);

        Vector2 movement = Vector2.ZERO;

        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            movement = movement.add(Vector2.LEFT);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            movement = movement.add(Vector2.RIGHT);
        }

        Vector2 topLeftCorner = getTopLeftCorner();
        if (topLeftCorner.x() < 0) {
            // out-of-bounds (left)
            setTopLeftCorner(new Vector2(0f, topLeftCorner.y()));
        } else if (topLeftCorner.x() + dimensions.x() > windowDimensions.x()) {
            // out-of-bounds (right)
            setTopLeftCorner(new Vector2(windowDimensions.x() - dimensions.x(), topLeftCorner.y()));
        }

        setVelocity(movement.mult(MOVEMENT_SPEED));
    }

    /**
     * Increments the collision count when the paddle collides with another object.
     *
     * @param other     The object the paddle collided with.
     * @param collision Details about the collision.
     */
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        collitions++;
    }

    /**
     * Returns the number of collisions the paddle has had.
     *
     * @return The total number of collisions.
     */
    @Override
    public int getCollisionCounter() {
        return collitions;
    }
}
