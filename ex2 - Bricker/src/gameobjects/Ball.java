package src.bricker.gameobjects;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.Sound;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.Random;

/**
 * Represents the ball object in the game. This class handles the ball's behavior
 * during collisions and includes sound effects for collision events.
 */
public class Ball extends GameObject implements Collidable {
    /**
     * The tag associated with the ball object.
     */
    public static final String TAG = "Ball";

    /**
     * The default speed of the ball in pixels per second.
     */
    private static final float BALL_SPEED = 200;

    /**
     * The sound to play when the ball collides with another object.
     */
    private Sound collisionSound;

    /**
     * The total number of collisions the ball has encountered.
     * This count is incremented each time the ball collides with another object.
     */
    private int collisions;

    /**
     * Constructs a new Ball instance.
     *
     * @param topLeftCorner  The top-left corner of the ball in window coordinates (pixels).
     * @param dimensions     The dimensions (width and height) of the ball.
     * @param renderable     The visual representation of the ball.
     * @param collisionSound The sound to play when the ball collides with another object.
     */
    public Ball(Vector2 topLeftCorner, Vector2 dimensions,
                Renderable renderable, Sound collisionSound) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionSound = collisionSound;
        setTag(TAG);
        setVelocity(randVelocity());
        collisions = 0;
    }

    /**
     * Called when the ball collides with another game object. It calculates
     * the new velocity of the ball based on the collision normal and plays a collision sound.
     *
     * @param other     The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        Vector2 newVelocity = getVelocity().flipped(collision.getNormal());
        setVelocity(newVelocity);
        collisionSound.play();

        collisions++;
    }

    /**
     * Retrieves the total number of collisions the ball has encountered.
     * This count is incremented each time the ball collides with another object.
     *
     * @return The total number of collisions.
     */
    public int getCollisionCounter() {
        return collisions;
    }

    /**
     * Generates a random velocity for the ball at the start or upon a specific event.
     * The velocity is determined randomly in both the horizontal and vertical directions
     * to ensure varied gameplay. The speed of the ball in both directions is determined
     * by the constant
     *
     * @return A Vector2 object representing the ball's velocity in pixels per second.
     */
    private Vector2 randVelocity() {
        Random random = new Random();
        float verticalVelocity = BALL_SPEED;
        float horizontalVelocity = BALL_SPEED;

        if (random.nextBoolean()) {
            verticalVelocity *= -1;
        }
        if (random.nextBoolean()) {
            horizontalVelocity *= -1;
        }

        return new Vector2(horizontalVelocity, verticalVelocity);
    }
}
