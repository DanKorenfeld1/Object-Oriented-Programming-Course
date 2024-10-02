package src.bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import src.bricker.main.BrickerGameManager;

/**
 * Represents a heart object in the game
 */
public class Heart extends GameObject {
    /**
     * The tags of objects with which the heart should not collide.
     */
    private final String[] tagsToAvoid;

    /**
     * The game manager responsible for managing game objects.
     */
    private BrickerGameManager brickerGameManager;

    /**
     * Constructs a new Heart instance.
     *
     * @param topLeftCorner The top-left corner of the heart in window coordinates.
     * @param dimensions    The dimensions of the heart.
     * @param renderable    The visual representation of the heart.
     * @param tagsToAvoid   Tags of objects with which the heart should not collide.
     */
    public Heart(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, String[] tagsToAvoid) {
        super(topLeftCorner, dimensions, renderable);
        this.tagsToAvoid = tagsToAvoid;
        brickerGameManager = null;
    }

    /**
     * Determines whether this heart should collide with another game object.
     *
     * @param other The other game object.
     * @return false if the other object's tag is in the tagsToAvoid array, true otherwise.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        for (String tag : tagsToAvoid) {
            if (tag.equals(other.getTag())) {
                return false;
            }
        }

        return true;
    }


    /**
     * Determines whether this heart should collide with another game object.
     *
     * @param other The other game object.
     * @return false if the other object's tag is in the tagsToAvoid array, true otherwise.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        if (other instanceof Paddle && other.getTag().equals(Paddle.TAG)) {
            brickerGameManager.addLife();
            brickerGameManager.removeObject(this);
        }
    }

    /**
     * Sets the game manager for this heart, enabling it to interact with the game state.
     *
     * @param brickerGameManager The game manager.
     */
    public void setBrickerGameManager(BrickerGameManager brickerGameManager) {
        this.brickerGameManager = brickerGameManager;
    }

    /**
     * Updates the state of the heart each frame, removing it from the game if it goes out of bounds.
     *
     * @param deltaTime Time since the last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (brickerGameManager != null) {
            Vector2 windowDimensions = brickerGameManager.getWindowDimantsions();
            Vector2 center = getCenter();
            if ((center.x() < 0 || center.x() > windowDimensions.x()) || (center.y() < 0 ||
                    center.y() > windowDimensions.y())) {
                // out-of-bounds
                brickerGameManager.removeObject(this);
            }
        }
    }
}
