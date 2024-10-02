package src.bricker.brick_strategies;

import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;
import src.bricker.gameobjects.Ball;
import src.bricker.main.BrickerGameManager;

import java.util.Random;

/**
 * a Factory for the creating instances of classes which implements CollisionStrategy.
 */
public class CollisionStrategyFactory {
    /**
     * The number of special strategies available.
     */
    private static final int NUMBER_OF_SPECIAL_STRATEGIES = 5;

    /**
     * The maximum number of capabilities a strategy can have.
     */
    private static final int MAX_CAPABILITIES = 3;

    /**
     * A random number generator used for strategy selection.
     */
    private Random rnd;

    /**
     * Constructor of the factory.
     */
    public CollisionStrategyFactory() {
        rnd = new Random();
    }

    /**
     * Builds a new CollisionStrategy based on the strategy which was picked randomly.
     * When the MultipleBehaviorCollisionStrategyDecorator is picked than it is built recursively.
     *
     * @param brickerGameManager the game manager, used by strategies to add & remove game objects from the
     *                           game such as
     *                           a new Puck or paddle.
     * @param imageReader        a reader for loading the immages of new displayable game objects which will
     *                           be added by
     *                           some strategies such as AddLifeCollisionStrategy.
     * @param soundReader        a reader for loading of soundtracks for new game objects which will be added
     *                           by some
     *                           strategies such as ExtraBallCollisionStrategy which requires the sound for
     *                           the puck's
     *                           collisions.
     * @param userInputListener  input listener which is required for ExtraPaddleCollisionStrategy.
     * @param ball               the main ball of the game required for tracking by the
     *                           CameraCollisionStrategy.
     * @return a newly built CollisionStrategy.
     */
    public CollisionStrategy build(BrickerGameManager brickerGameManager,
                                   ImageReader imageReader, SoundReader soundReader,
                                   UserInputListener userInputListener, Ball ball) {
        if (rnd.nextBoolean()) {
            return new BasicCollisionStrategy(brickerGameManager);
        } else {
            return buildSpecialCollisionStrategy(brickerGameManager, imageReader, soundReader,
                    userInputListener, ball, 0);
        }
    }

    private CollisionStrategy buildSpecialCollisionStrategy(BrickerGameManager brickerGameManager,
                                                            ImageReader imageReader, SoundReader soundReader,
                                                            UserInputListener userInputListener, Ball ball,
                                                            int capabilities) {
        switch (rnd.nextInt(NUMBER_OF_SPECIAL_STRATEGIES)) {
            case 0:
                return getExtraBallCollisionStrategy(brickerGameManager, imageReader, soundReader);
            case 1:
                return getAddLifeCollisionStrategy(brickerGameManager, imageReader);
            case 2:
                return getExtraPaddleCollisionStrategy(brickerGameManager, imageReader, userInputListener);
            case 3:
                return getCameraCollisionStrategy(brickerGameManager, ball);
            case 4:
                return getMultipleBehaviorCollisionStrategyDecorator(brickerGameManager, imageReader,
                        soundReader, userInputListener, ball, capabilities);
            default: //
                // this should never happen.
                return null;
        }
    }


    /**
     * Recursively build the binray tree of strategies.
     * Each instance of MultipleBehaviorCollisionStrategyDecorator is considered as a node (not a leaf) in
     * the tree
     * and each "normal" strategy is a leaf.
     * The tree is built from down-right leftwards and then upwards while maintaining that the amount of
     * "normal"
     * strategies doesn't exceed a threshold.
     * When the threshold is reached the rest of the tree is filled with null.
     *
     * @param brickerGameManager the game manager, used by strategies to add & remove game objects from the
     *                           game such as
     *                           a new Puck or paddle.
     * @param imageReader        a reader for loading the immages of new displayable game objects which will
     *                           be added by
     *                           some strategies such as AddLifeCollisionStrategy.
     * @param soundReader        a reader for loading of soundtracks for new game objects which will be added
     *                           by some
     *                           strategies such as ExtraBallCollisionStrategy which requires the sound for
     *                           the puck's
     *                           collisions.
     * @param userInputListener  input listener which is required for ExtraPaddleCollisionStrategy.
     * @param ball               the main ball of the game required for tracking by the
     *                           CameraCollisionStrategy.
     * @return a newly built MultipleBehaviorCollisionStrategyDecorator.
     */
    private MultipleBehaviorCollisionStrategyDecorator getMultipleBehaviorCollisionStrategyDecorator(
            BrickerGameManager brickerGameManager, ImageReader imageReader, SoundReader soundReader,
            UserInputListener userInputListener, Ball ball, int capabilities) {
        int added_caps;

        if (capabilities >= MAX_CAPABILITIES) {
            return null;
        }
        CollisionStrategy right = buildSpecialCollisionStrategy(brickerGameManager, imageReader,
                soundReader, userInputListener, ball, capabilities);
        if (right == null) {
            added_caps = 0;
        } else if (right instanceof MultipleBehaviorCollisionStrategyDecorator) {
            added_caps = ((MultipleBehaviorCollisionStrategyDecorator) right).getCapabilities();
        } else {
            // all the special capabilities, but not too special as in multiple...
            added_caps = 1;
        }
        CollisionStrategy left = buildSpecialCollisionStrategy(brickerGameManager, imageReader,
                soundReader, userInputListener, ball, capabilities + added_caps);
        return new MultipleBehaviorCollisionStrategyDecorator(right, left);
    }

    private static AddLifeCollisionStrategy getAddLifeCollisionStrategy(BrickerGameManager brickerGameManager,
                                                                        ImageReader imageReader) {
        ImageRenderable heartImage = imageReader.readImage("assets/heart.png", true);
        Vector2 heartDims = new Vector2(BrickerGameManager.LIVES_DISPLAY_HEIGHT,
                BrickerGameManager.LIVES_DISPLAY_HEIGHT);
        return new AddLifeCollisionStrategy(brickerGameManager,
                heartDims, heartImage);
    }

    private static CameraCollisionStrategy getCameraCollisionStrategy(BrickerGameManager brickerGameManager,
                                                                      Ball ball) {
        return new CameraCollisionStrategy(brickerGameManager, ball);
    }

    private static ExtraPaddleCollisionStrategy getExtraPaddleCollisionStrategy(
            BrickerGameManager brickerGameManager, ImageReader imageReader,
            UserInputListener userInputListener) {
        ImageRenderable paddleImage = imageReader.readImage("assets/paddle.png", true);
        Vector2 paddleDims = new Vector2(BrickerGameManager.PADDLE_WIDTH,
                BrickerGameManager.PADDLE_HEIGHT);
        return new ExtraPaddleCollisionStrategy(brickerGameManager, paddleDims, paddleImage,
                userInputListener);
    }

    private static ExtraBallCollisionStrategy getExtraBallCollisionStrategy(
            BrickerGameManager brickerGameManager, ImageReader imageReader, SoundReader soundReader) {
        Sound collisionSound = soundReader.readSound("assets/blop_cut_silenced.wav");
        ImageRenderable puckImage = imageReader.readImage("assets/mockBall.png", true);
        Vector2 puckDims = new Vector2(BrickerGameManager.PUCK_RADUIS,
                BrickerGameManager.PUCK_RADUIS);
        return new ExtraBallCollisionStrategy(brickerGameManager, puckDims, puckImage, collisionSound);
    }
}
