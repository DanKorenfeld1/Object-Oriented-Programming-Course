package src.bricker.brick_strategies;

import danogl.GameObject;

/**
 * Defines the CollisionStrategy interface. This interface is used to implement different
 * strategies for handling collisions between game objects in the Bricker game.
 * Implementing classes define specific behaviors that occur when a collision is detected.
 */
public interface CollisionStrategy {
    /**
     * Method to be implemented by classes implementing this interface.
     * It defines the behavior to be executed when a collision occurs between two game objects.
     *
     * @param thisObj The game object implementing the collision strategy, typically a brick.
     * @param otherObj The other game object involved in the collision.
     */
    void onCollision(GameObject thisObj, GameObject otherObj);
}
