package src.bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import src.bricker.brick_strategies.CollisionStrategy;

/**
 * Represents a brick object in the game. Bricks are game objects that can interact
 * with other objects, particularly the ball. When a collision with a ball occurs,
 * a specific collision strategy is executed.
 */
public class Brick extends GameObject {

    /**
     * The tag associated with the brick object.
     */
    public static final String TAG = "Brick";

    /**
     * The collision strategy to execute when a collision with a ball occurs.
     */
    private final CollisionStrategy collisionStrategy;

    /**
     * A flag indicating whether the brick has already collided with a ball.
     */
    private boolean alreadyCollided;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Brick(Vector2 topLeftCorner, Vector2 dimensions,
                 Renderable renderable, CollisionStrategy collisionStrategy) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionStrategy = collisionStrategy;
        alreadyCollided = false;
    }

    /**
     * Called when this brick collides with another game object. If the other object
     * is an instance of Ball, the defined collision strategy is executed.
     *
     * @param other     The GameObject instance this object collided with.
     * @param collision Details about the collision event.
     */
    public void onCollisionEnter(GameObject other, Collision collision) {
        if (!alreadyCollided && other instanceof Ball) {
            collisionStrategy.onCollision(this, other);
            alreadyCollided = true;
        }
    }


    /**
     * Updates the state of the brick each frame.
     *
     * @param deltaTime Time since last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        alreadyCollided = false;
    }
}
