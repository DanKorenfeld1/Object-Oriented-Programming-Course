package src.bricker.lives_display;

import danogl.GameObject;
import danogl.util.Vector2;

/**
 * Defines the contract for displaying lives or similar metrics in a game.
 */
public interface LivesDisplay {
    /**
     * Updates the display based on the current number of lives or the metric being tracked.
     *
     * @param currentLives The current number of lives or value of the metric to display.
     */
    void notify(int currentLives);

    /**
     * Retrieves the game objects associated with this display, allowing them to be managed or rendered.
     *
     * @return An array of GameObjects that are part of this display.
     */
    GameObject[] getGameObjects();

    /**
     * Gets the current status of each object in the display, such as whether they should be
     * added, removed, or are already displayed.
     *
     * @return An array of DisplayStatus indicating the status of each object in the display.
     */
    DisplayStatus[] getObjectsStatus();

    /**
     * Sets the top-left corner position for the entire display, typically used
     * for positioning the display on the screen.
     *
     * @param topLeftCorner The top-left corner position for the display.
     */
    void setTopLeftCorner(Vector2 topLeftCorner);
}
