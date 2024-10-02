package src.bricker.brick_strategies;

import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;
import src.bricker.gameobjects.*;
import src.bricker.main.BrickerGameManager;

/**
 * This strategy creates a new paddle (no more than 1 at all times) when
 * the brick is being hit.
 */
public class ExtraPaddleCollisionStrategy extends BasicCollisionStrategy {
    /**
     * The tag of all extra paddle, which is used for distinguishing between
     * the extra paddles and the original paddle.
     */
    public static final String EXTRA_PADDLE_TAG = "ExtraPaddle";

    /**
     * The user input listener object used by the extra paddle for listening for movement
     * commands from the user.
     */
    private UserInputListener inputListener;

    /**
     * The dimensions of the paddle object.
     */
    private Vector2 paddleDimensions;

    /**
     * The dimensions of the game window.
     */
    private Vector2 windowDimensions;

    /**
     * The image of the paddle object.
     */
    private ImageRenderable imageRenderable;

    /**
     * The game manager used later to remove the brick from the game.
     */
    private BrickerGameManager brickerGameManager;

    /**
     * A flag indicating whether an extra paddle is currently displayed.
     */
    private static boolean isDisplyed = false;

    /**
     * The number of collisions after which a new paddle is no longer added.
     */
    private static final int COLLISIONS_THRESHOLD = 4;

    /**
     * Construct a new ExtraPaddleCollisionStrategy object.
     *
     * @param brickerGameManager the game manager used later to remove the brick from the game.
     * @param paddleDimensions the dimensions of the paddle object.
     * @param imageRenderable the image of the paddle object.
     * @param inputListener the user input listener object used by the extra paddle for listening
     *                      for movement commands from the user.
     */
    public ExtraPaddleCollisionStrategy(BrickerGameManager brickerGameManager, Vector2 paddleDimensions,
                                        ImageRenderable imageRenderable,
                                        UserInputListener inputListener) {
        super(brickerGameManager);
        this.brickerGameManager = brickerGameManager;
        this.paddleDimensions = paddleDimensions;
        this.imageRenderable = imageRenderable;
        this.windowDimensions = brickerGameManager.getWindowDimantsions();
        this.inputListener = inputListener;
    }

    /**
     * Defines the action to be taken when a collision occurs. Specifically, add a new
     * paddle to the game in the middle of the window.
     *
     * @param thisObj The GameObject involved in the collision, typically a brick in this context.
     * @param otherObj The other GameObject involved in the collision.
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
        super.onCollision(thisObj, otherObj);

        if(!isDisplyed) { //add the paddle
            Paddle anotherPaddle = new Paddle(Vector2.ZERO, paddleDimensions,imageRenderable,
                    inputListener,windowDimensions);
            anotherPaddle.setTag(EXTRA_PADDLE_TAG);
            anotherPaddle.setCenter(windowDimensions.mult(0.5f));
            brickerGameManager.addObject(anotherPaddle);
            Callable action = (BrickerGameManager manager, GameObject trackedObject) -> {
                manager.removeObject(trackedObject);
                ExtraPaddleCollisionStrategy.reset();
            };
            CollisionTracker collisionTracker = new CollisionTracker(anotherPaddle,anotherPaddle,
                    COLLISIONS_THRESHOLD,
                    action, brickerGameManager);
            brickerGameManager.addObject(collisionTracker);
            isDisplyed = true;
        }
    }

    /**
     * Reset the static flag which blocks new paddles from being created.
     * This is used by the action passed to the CollisionTracker & BrickerGameManager when
     * the game is reset to avoid the creation of new paddles in the new game even if
     * the previous game has ended with a displayed extra paddle.
     */
    public static void reset() {
        isDisplyed = false;
    }
}
