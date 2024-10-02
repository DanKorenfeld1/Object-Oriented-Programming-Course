package src.pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Facilitates the simulation of a night effect within a game world.
 * This class is responsible for creating and managing a GameObject that acts as a night overlay,
 * transitioning in opaqueness to represent the day-night cycle.
 */
public class Night {

    /**
     * The tag identifier for the GameObject representing the night.
     * Utilized for easy identification and manipulation within the game's object manager.
     */
    private static final String TAG = "night";

    /**
     * The initial opaqueness level of the night overlay, signifying the transition's start,
     * typically from day to night. This value denotes a fully transparent state.
     */
    private static final float INITIAL_OPAQUENESS = 0f;

    /**
     * The final opaqueness level of the night overlay,
     * indicating the transition's end or the peak of the night cycle.
     * This value denotes a semi-transparent state, simulating the effect of nighttime.
     */
    private static final float FINAL_OPAQUENESS = 0.5f;

    /**
     * Defines the division factor to calculate the half-cycle duration from the total day-night cycle length.
     * This constant is used to adjust the timing of the opaqueness transition to simulate the gradual change
     * from day to night and back.
     */
    private static final int HALF_CYCLE = 2;


    /**
     * Creates and returns a GameObject representing the night overlay.
     * This object transitions in opaqueness to simulate the night effect over a given cycle length.
     *
     * @param windowDimensions The dimensions of the game window,
     *                         ensuring the night overlay covers the entire screen.
     * @param cycleLength      The total length of the day-night cycle,
     *                         used to determine the duration of the night effect's transition phases.
     * @return A GameObject configured to represent the night effect,
     * ready to be added to the game environment.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        RectangleRenderable rectangleRenderable = new RectangleRenderable(Color.BLACK);
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions, rectangleRenderable);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(TAG);

        Transition<Float> transition = new Transition<Float>(
                night,
                night.renderer()::setOpaqueness,
                INITIAL_OPAQUENESS,
                FINAL_OPAQUENESS,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                halfOfCycle(cycleLength),
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );

        return night;
    }

    /**
     * Calculates and returns half the duration of the day-night cycle based on the provided total cycle
     * length. This value is used to determine the transition speed of the night effect's opaqueness.
     *
     * @param cycleLength The total length of the day-night cycle.
     * @return The duration of half the cycle, used in opaqueness transitions.
     */
    private static float halfOfCycle(float cycleLength) {
        return cycleLength / HALF_CYCLE;
    }
}
