package src.bricker.brick_strategies;

import danogl.GameObject;

/**
 * a Decorator around CollisionStrategyDecorator which lets to use more than one CollisionStrategy at once.
 */
public class MultipleBehaviorCollisionStrategyDecorator extends CollisionStrategyDecorator {
    /**
     * The maximum amount of collision behaviors allowed in the decorator.
     */
    private static final int MAX_AMOUNT_OF_BEHAVIORS = 3;

    /**
     * The first collision strategy to be used.
     */
    private CollisionStrategy right;

    /**
     * The second collision strategy to be used.
     */
    private CollisionStrategy left;

    /**
     * Constructor.
     *
     * @param right a collision strategy to be used.
     * @param left  another collision strategy to be used.
     */
    public MultipleBehaviorCollisionStrategyDecorator(CollisionStrategy right, CollisionStrategy left) {
        this.right = right;
        this.left = left;
    }

    /**
     * Get the total amount of capabilities of the entire tree (recursively).
     *
     * @return the total amount of capabilities of the entire tree.
     */
    public int getCapabilities() {
        int result = 0;
        for (CollisionStrategy s : new CollisionStrategy[]{right, left}) {
            if (s != null) {
                if (s instanceof MultipleBehaviorCollisionStrategyDecorator) {
                    result += ((MultipleBehaviorCollisionStrategyDecorator) s).getCapabilities();
                } else {
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * Defines the action to be taken when a collision occurs. Specifically, use the 2 provided
     * CollisionStrategy
     * object's onCollision methods.
     *
     * @param thisObj  The game object implementing the collision strategy, typically a brick.
     * @param otherObj The other game object involved in the collision.
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
        super.onCollision(thisObj, otherObj);
        for (CollisionStrategy s : new CollisionStrategy[]{right, left}) {
            if (s != null) {
                s.onCollision(thisObj, otherObj);
            }
        }
    }
}
