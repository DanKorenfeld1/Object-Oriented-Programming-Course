package src.pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Utility class for creating a sky background in the game. The sky is represented
 * as a single, large rectangle that covers the entire game window and moves with the camera,
 * providing a static, unchanging background that simulates the appearance of the sky.
 */
public class Sky {
    /**
     * The basic color of the sky, represented as a hexadecimal color code.
     */
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

    /**
     * The tag used to identify the sky GameObject within the game.
     */
    private static final String TAG = "sky";

    /**
     * Creates and returns a new GameObject representing the sky. This object is
     * rendered as a large rectangle that fills the entire game window and moves
     * with the camera, ensuring it remains in the background.
     *
     * @param windowDimensions The dimensions of the game window, used to size the sky
     *                         rectangle so it fully covers the window.
     * @return A GameObject configured to represent the sky, complete with a solid color
     * fill and the correct coordinate space setting to move with the camera.
     */
    public static GameObject create(Vector2 windowDimensions) {
        GameObject sky = new GameObject(Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sky.setTag(TAG);
        return sky;
    }
}
