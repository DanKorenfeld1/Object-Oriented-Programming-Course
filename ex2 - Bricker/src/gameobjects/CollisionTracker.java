package src.bricker.gameobjects;

import danogl.GameManager;
import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import src.bricker.main.BrickerGameManager;

/**
 * Tracks collisions of a specific object and triggers an action when a collision threshold is reached.
 */
public class CollisionTracker extends GameObject {
    /**
     * The object whose collisions are to be tracked.
     */
    private Collidable collidableToTrack;

    /**
     * The game object associated with the tracked collisions.
     */
    private GameObject objectToTrack;

    /**
     * The number of collisions after which an action is triggered.
     */
    private int collisions;

    /**
     * The collision threshold after which an action is triggered.
     */
    private final int collisionsThreshold;

    /**
     * The action to be performed upon reaching the collision threshold.
     */
    private final Callable action;

    /**
     * The game manager responsible for managing game objects.
     */
    private BrickerGameManager manager;

    /**
     * Initializes a new instance of CollisionTracker.
     *
     * @param collidableToTrack   The object whose collisions are to be tracked.
     * @param objectToTrack       The game object associated with the tracked collisions.
     * @param collisionsThreshold The number of collisions after which an action is triggered.
     * @param action              The action to be performed upon reaching the collision threshold.
     * @param manager             The game manager responsible for managing game objects.
     */
    public CollisionTracker(Collidable collidableToTrack, GameObject objectToTrack, int collisionsThreshold,
                            Callable action,
                            BrickerGameManager manager) {
        super(Vector2.ZERO, Vector2.ONES, null);
        this.collidableToTrack = collidableToTrack;
        this.objectToTrack = objectToTrack;
        this.manager = manager;
        collisions = collidableToTrack.getCollisionCounter();
        this.collisionsThreshold = collisionsThreshold;
        this.action = action;
    }

    /**
     * Updates the state of the tracker each frame, checking if the collision threshold has been met and
     * executing the action if so.
     *
     * @param deltaTime Time since the last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (collidableToTrack.getCollisionCounter() - collisions >= collisionsThreshold) {
            action.call(manager, objectToTrack);
            // stop tracking after threshold is reached.
            manager.removeObject(this);
        }
    }
}
