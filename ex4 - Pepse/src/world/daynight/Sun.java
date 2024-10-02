package src.pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.function.Function;

/**
 * Represents the sun in a day/night cycle within a game environment.
 * This class manages the creation and animation of a GameObject that simulates
 * the sun's movement across the sky, based on a specified cycle length.
 */
public class Sun {

    /**
     * The tag identifier for the GameObject representing the sun,
     * allowing for easy identification and manipulation within the game's object manager.
     */
    private static final String TAG = "sun";

    /**
     * The initial angle of the sun's movement cycle, typically representing the sunrise position.
     */
    private static final float INITIAL_ANGLE = 0f;

    /**
     * The final angle of the sun's movement cycle, completing a full rotation to simulate a day's passage.
     */
    private static final float FINAL_ANGLE = 360f;

    /**
     * Used to divide dimensions and calculate centers, representing a halfway point or division factor.
     */
    private static final int HALF = 2;

    /**
     * Determines the initial vertical position of the sun as a ratio of the window's height,
     * affecting the starting height of the sun's arc across the sky.
     */
    private static final int SUN_INITIAL_Y_RATIO = 6;

    /**
     * The radius of the sun, affecting the size of the sun's representation in the game world.
     */
    private static final int RADIUS = 150;

    /**
     * a callback used for determining the center of the sun's rotation circle.
     */
    private static Function<Float, Float> horizonHeightRetriever = null;

    /**
     * set the method used by the Sun.create for determining the height of the horizon, i.e. the
     * height of the terrain in the middle of the window.
     *
     * @apiNote if this callback isn't set before calling Sun.create than the height of the horizon
     * will be assumed to be half of the window height.
     *
     * @param callback a function used for determining the height of the horizon.
     */
    public static void setHorizonHeightRetriever(Function<Float, Float> callback) {
        Sun.horizonHeightRetriever = callback;
    }

    /**
     * Creates and returns a GameObject representing the sun.
     * This object animates across the sky, simulating the sun's movement based on the given cycle length.
     * The sun follows a circular path centered in the window,
     * starting and ending its cycle at the specified initial and final angles.
     *
     * @apiNote since we are expected to expose a static method with this exact signature and required
     *          to position the sun according to the height of the random terrain which can't be passed
     *          without changing the method's signature we've added Sun.setHorizonHeightRetriever method
     *          so we could obtain that knowledge without breaking the required API.
     *
     * @param windowDimensions The dimensions of the game window,
     *                         used to determine the path and position of the sun.
     * @param cycleLength      The duration of the sun's movement cycle,
     *                         defining how long it takes for the sun to complete one full rotation.
     * @return A GameObject configured to represent the sun, including its animation across the sky.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        OvalRenderable ovalRenderable = new OvalRenderable(Color.YELLOW);
        GameObject sun = new GameObject(Vector2.ZERO, new Vector2(RADIUS, RADIUS), ovalRenderable);
        Vector2 center = getInitialCenter(windowDimensions);
        sun.setCenter(center);
        sun.setTag(TAG);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        Vector2 cycleCenter = getCycleCenter(windowDimensions);

        Transition<Float> transition = new Transition<Float>(
                sun,
                (Float angle) -> sun.setCenter(center.subtract(cycleCenter).rotated(angle).add(cycleCenter)),
                INITIAL_ANGLE,
                FINAL_ANGLE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null
        );

        return sun;
    }

    /**
     * calculate the intial center of the sun when it's possible, otherwise assume
     * its position to be the center of the sky, which is middle of the horizontal
     * size of the window & a quarter below the top of the window.
     *
     * @param windowDimensions dimensions of the game window.
     * @return initial center of the sun.
     */
    private static Vector2 getInitialCenter(Vector2 windowDimensions) {
        float middle = windowDimensions.x() / HALF;
        if (Sun.horizonHeightRetriever == null) {
            // assume that the horizon is at half of the window height.
            return new Vector2(middle, windowDimensions.y() / SUN_INITIAL_Y_RATIO);
        } else {
            return new Vector2(middle, Sun.horizonHeightRetriever.apply(middle) / HALF);
        }
    }

    /**
     * calculate the center of the sun rotation when it's possible, otherwise assume
     * its position to be the center of the window.
     *
     * @param windowDimensions dimensions of the game window.
     * @return center of the sun's rotation.
     */
    private static Vector2 getCycleCenter(Vector2 windowDimensions) {
        float middle = windowDimensions.x() / HALF;
        if (Sun.horizonHeightRetriever == null) {
            // assume that the horizon is at half of the window height.
            return new Vector2(middle, windowDimensions.y() / HALF);
        } else {
            return new Vector2(middle, Sun.horizonHeightRetriever.apply(middle));
        }
    }
}
