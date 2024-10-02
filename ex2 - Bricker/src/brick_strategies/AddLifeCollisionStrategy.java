package src.bricker.brick_strategies;

import danogl.GameObject;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;
import src.bricker.gameobjects.Ball;
import src.bricker.gameobjects.Brick;
import src.bricker.gameobjects.Heart;
import src.bricker.gameobjects.Puck;
import src.bricker.main.BrickerGameManager;

/**
 * This is a strategy that creates a new heart instead of the collided brick
 * when the brick is being hit by a ball/puck.
 */
public class AddLifeCollisionStrategy extends BasicCollisionStrategy {

    /**
     * Constructs a new Velocity of the heart that fall down
     */
    private static final int VELOCITY = 100;

    /**
     * The dimensions of the heart object which will be created.
     */
    private Vector2 heartDims;

    /**
     * The image of the heart object which will be created.
     */
    private ImageRenderable heartImage;

    /**
     * The game manager used for removing the brick when it being hit.
     */
    private BrickerGameManager brickerGameManager;

    /**
     * The tags of objects to avoid collision with.
     */
    private String[] tagsToAvoid;

    /**
     * Constructs a new AddLifeCollisionStrategy.
     *
     * @param brickerGameManager the game manager used for removing the brick when it being hit.
     * @param heartDims          the dimensions of the heart object which will be created.
     * @param heartImage         the image of the heart object which will be created.
     */
    public AddLifeCollisionStrategy(BrickerGameManager brickerGameManager, Vector2 heartDims,
                                    ImageRenderable heartImage) {
        super(brickerGameManager);
        this.brickerGameManager = brickerGameManager;
        tagsToAvoid = new String[]{
                Ball.TAG, Puck.TAG, ExtraPaddleCollisionStrategy.EXTRA_PADDLE_TAG, Brick.TAG
        };
        this.heartImage = heartImage;
        this.heartDims = heartDims;
    }

    /**
     * Defines the action to be taken when a collision occurs. Specifically,
     * create a new heart object & adds it to the game when the brick is being hit.
     *
     * @param thisObj  The GameObject involved in the collision, typically a brick in this context.
     * @param otherObj The other GameObject involved in the collision.
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
        super.onCollision(thisObj, otherObj);

        Heart heart = new Heart(Vector2.ZERO, heartDims, heartImage, tagsToAvoid);
        heart.setCenter(thisObj.getCenter());
        heart.setVelocity(Vector2.DOWN.mult(VELOCITY));
        heart.setBrickerGameManager(brickerGameManager);
        brickerGameManager.addObject(heart);
    }
}
