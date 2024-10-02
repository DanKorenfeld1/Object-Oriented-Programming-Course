package src.bricker.brick_strategies;

import danogl.GameObject;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import src.bricker.gameobjects.Ball;
import src.bricker.gameobjects.Callable;
import src.bricker.gameobjects.CollisionTracker;
import src.bricker.main.BrickerGameManager;

/**
 * This is a strategy that shifts the camera focus to the main ball for a constant amount of
 * collisions before shifting back the camera's focus to the center of the window.
 */
public class CameraCollisionStrategy extends BasicCollisionStrategy {
    /**
     * The ball to track its movement.
     */
    private Ball ball;

    /**
     * The game manager used later for removing the brick from the game.
     */
    private BrickerGameManager brickerGameManager;

    /**
     * The factor by which to multiply the window dimensions to determine the camera focus area.
     */
    private static final float FACTOR = 1.2f;

    /**
     * The number of collisions after which the camera focus is shifted back to the center of the window.
     */
    private static final int COLLISIONS_THRESHOLD = 4;

    /**
     * Constructs a new CameraCollisionStrategy.
     *
     * @param brickerGameManager the game manager used later for removing the brick from the game.
     * @param ball               the ball to track it's movement.
     */
    public CameraCollisionStrategy(BrickerGameManager brickerGameManager, Ball ball) {
        super(brickerGameManager);
        this.brickerGameManager = brickerGameManager;
        this.ball = ball;
    }

    /**
     * Defines the action to be taken when a collision occurs. Specifically, it shifts the
     * camera focus from the center of the window to the center of the ball.
     *
     * @param thisObj  The game object implementing the collision strategy, typically a brick.
     * @param otherObj The other game object involved in the collision.
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
        super.onCollision(thisObj, otherObj);

        if (brickerGameManager.camera() == null &&
                (otherObj instanceof Ball && otherObj.getTag().equals(ball.getTag()))) {
            Camera camera = new Camera(ball,
                    Vector2.ZERO,
                    brickerGameManager.getWindowDimantsions().mult(FACTOR),
                    brickerGameManager.getWindowDimantsions());
            brickerGameManager.setCamera(camera);

            Callable action = (BrickerGameManager manager, GameObject trackedObject) -> {
                manager.setCamera(null);
            };
            CollisionTracker collisionTracker = new CollisionTracker(ball, ball, COLLISIONS_THRESHOLD,
                    action, brickerGameManager);
            brickerGameManager.addObject(collisionTracker);
        }
    }
}
