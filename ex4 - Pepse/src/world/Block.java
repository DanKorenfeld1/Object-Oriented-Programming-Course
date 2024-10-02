package src.pepse.world;

import danogl.GameManager;
import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a block in the game world. Blocks are the basic building units of the terrain
 * and other static structures within the game. A Block is an immovable object that can be
 * used to construct landscapes, obstacles, and platforms for the Avatar to interact with.
 */
public class Block extends GameObject {
    /**
     * The size of each side of the block in pixels. This is a square block, so height
     * and width are equal.
     */
    public static final int SIZE = 30;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }
}
