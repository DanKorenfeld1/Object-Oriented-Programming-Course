package src.bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.Layer;
import src.bricker.main.BrickerGameManager;

/**
 * Implements a basic collision strategy for bricks in the game. When a brick collides
 * with another object, this strategy removes the brick from the game.
 */
public class BasicCollisionStrategy implements CollisionStrategy {
    /**
     * The game manager used for removing the brick when it being hit.
     */
    private BrickerGameManager brickerGameManager;

    /**
     * Constructor for BasicCollisionStrategy.
     *
     * @param brickerGameManager The game manager instance, used to interact with the game's state,
     *                           such as removing game objects during collisions.
     */
    public BasicCollisionStrategy (BrickerGameManager brickerGameManager) {
        this.brickerGameManager = brickerGameManager;
    }

    /**
     * Defines the action to be taken when a collision occurs. Specifically, removes the brick
     * (thisObj) from the game when it collides with another object.
     *
     * @param thisObj The GameObject involved in the collision, typically a brick in this context.
     * @param otherObj The other GameObject involved in the collision.
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
            brickerGameManager.removeObject(thisObj, Layer.STATIC_OBJECTS);
    }
}
