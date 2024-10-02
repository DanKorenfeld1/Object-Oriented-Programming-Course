package src.bricker.gameobjects;


/**
 * Represents an object that can collide with others in the game.
 */
public interface Collidable {
    /**
     * Returns the number of collisions this object has encountered.
     *
     * @return The total number of collisions.
     */
    int getCollisionCounter();
}
