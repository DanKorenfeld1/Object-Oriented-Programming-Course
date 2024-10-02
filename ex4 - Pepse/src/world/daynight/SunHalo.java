package src.pepse.world.daynight;

import danogl.GameObject;
import danogl.components.Component;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Facilitates the creation of a halo effect around the sun within a game environment.
 * This class is responsible for generating a translucent overlay that follows the sun,
 * simulating a glow or halo effect that enhances the visual representation of the sun.
 */
public class SunHalo {

    /**
     * The size of the halo effect, determining the dimensions of the halo GameObject.
     * This size should ideally be larger than the sun itself to create a visible glow around it.
     */
    private static final Vector2 HALO_SIZE = new Vector2(275, 275);

    /**
     * The color of the halo effect, including its alpha transparency to ensure the halo appears as a subtle
     * glow around the sun. The chosen color is a light yellow with low opacity.
     */
    private static final Color HALO_COLOR = new Color(255, 255, 0, 20);

    /**
     * Creates and returns a GameObject representing the sun's halo.
     * The halo is designed to continuously align its center with the sun's center,
     * ensuring it appears as a consistent glow around the sun throughout the day/night cycle.
     *
     * @param sun The GameObject representing the sun, to which the halo effect will be aligned.
     * @return A GameObject configured to represent the sun's halo,
     * including behavior to maintain its position relative to the sun.
     */
    public static GameObject create(GameObject sun) {
        OvalRenderable renderable = new OvalRenderable(HALO_COLOR);
        GameObject halo = new GameObject(Vector2.ZERO, HALO_SIZE, renderable);

        Component alignCenters = new Component() {
            @Override
            public void update(float deltaTime) {
                halo.setCenter(sun.getCenter());
            }
        };
        halo.addComponent(alignCenters);

        return halo;
    }
}
