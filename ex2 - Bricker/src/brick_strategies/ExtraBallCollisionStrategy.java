package src.bricker.brick_strategies;

import danogl.GameObject;
import danogl.gui.Sound;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;
import src.bricker.gameobjects.Puck;
import src.bricker.main.BrickerGameManager;

/**
 * This strategy creates a new ball (puck) when the brick is being hit.
 */
public class ExtraBallCollisionStrategy extends BasicCollisionStrategy {
    /**
     * The sound emitted when the puck object collides with another object.
     */
    private Sound sound;

    /**
     * The image of the puck object.
     */
    private ImageRenderable imageRenderable;

    /**
     * The dimensions of the puck object.
     */
    private Vector2 puckDimensions;

    /**
     * The game manager used later to remove the brick from the game.
     */
    private BrickerGameManager brickerGameManager;

    /**
     * The number of pucks to be created upon collision.
     */
    private final static int NUMBER_PUCKS = 2;

    /**
     * Constructs a new ExtraBallCollisionStrategy.
     *
     * @param brickerGameManager the game manager used later to remove the brick from the game.
     * @param puckDimensions     the dimensions of the puck object.
     * @param imageRenderable    the image of the puck object.
     * @param sound              the sound emitted when the puck object collides with another object.
     */
    public ExtraBallCollisionStrategy(BrickerGameManager brickerGameManager, Vector2 puckDimensions,
                                      ImageRenderable imageRenderable, Sound sound) {
        super(brickerGameManager);
        this.brickerGameManager = brickerGameManager;
        this.puckDimensions = puckDimensions;
        this.imageRenderable = imageRenderable;
        this.sound = sound;
    }

    /**
     * Defines the action to be taken when a collision occurs. Specifically, create a new
     * Puck object & add it to the game.
     *
     * @param thisObj  The game object implementing the collision strategy, typically a brick.
     * @param otherObj The other game object involved in the collision.
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
        super.onCollision(thisObj, otherObj);

        for (int i = 0; i < NUMBER_PUCKS; i++) {
            Puck puck = new Puck(thisObj.getCenter(), puckDimensions, imageRenderable, sound,
                    brickerGameManager);
            brickerGameManager.addObject(puck);
        }

    }
}
