package src.bricker.brick_strategies;

import danogl.GameObject;

/**
 * A Decorator around CollisionStrategy.
 * This class is abstract so it wouldn't be possible to create an instance of this
 * specific class, i.e. without using a class which derives from this one.
 */
public abstract class CollisionStrategyDecorator implements CollisionStrategy {
    /**
     * Constructor - create a new CollisionStrategyDecorator.
     */
    public CollisionStrategyDecorator() {};

    /**
     * Decorates the wrapped OnCollision.
     * This method defines the behavior to be executed when a collision occurs between two game objects.
     *
     * @param thisObj The game object implementing the collision strategy, typically a brick.
     * @param otherObj The other game object involved in the collision.
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
    }
}
