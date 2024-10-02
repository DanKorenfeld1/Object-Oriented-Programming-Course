package src.bricker.lives_display;

import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.Color;

/**
 * Represents a numerical display for lives or a similar count, changing color based on the count's value.
 */
public class NumericalDisplay extends GameObject implements LivesDisplay {
    /**
     * The threshold for the red alert, indicating a critical low number of lives.
     */
    private static final int RED_ALERT = 1;

    /**
     * The threshold for the yellow alert, indicating a cautionary low number of lives but not as critical as
     * red alert.
     */
    private static final int YELLOW_ALERT = 2;

    /**
     * Indicates whether this display has already been added to the game objects for rendering.
     */
    private boolean already_added;

    /**
     * The TextRenderable component used to display the numerical count.
     */
    private TextRenderable text;

    /**
     * An array of tags representing the GameObjects that this display should not interact with.
     */
    private final String[] tagsToAvoid;


    /**
     * Constructs a NumericalDisplay instance with a specified position, dimensions, and initial text.
     *
     * @param topLeftCorner Position of the display in window coordinates.
     * @param dimensions    Dimensions of the display.
     * @param text          The text renderable used for displaying the number.
     * @param tagsToAvoid   Tags of objects that this display should avoid colliding with.
     */
    public NumericalDisplay(Vector2 topLeftCorner, Vector2 dimensions,
                            TextRenderable text, String[] tagsToAvoid) {
        super(topLeftCorner, dimensions, text);
        this.text = text;
        this.tagsToAvoid = tagsToAvoid;
        already_added = false;
    }

    /**
     * Constructs a NumericalDisplay with default text.
     *
     * @param topLeftCorner Position of the display in window coordinates.
     * @param dimensions    Dimensions of the display.
     * @param tagsToAvoid   Tags of objects that this display should avoid colliding with.
     */
    public NumericalDisplay(Vector2 topLeftCorner, Vector2 dimensions, String[] tagsToAvoid) {
        this(topLeftCorner, dimensions, new TextRenderable(""), tagsToAvoid);
    }

    /**
     * Returns an array containing this numerical display as the only GameObject.
     *
     * @return An array of GameObjects containing only this display.
     */
    public GameObject[] getGameObjects() {
        return new GameObject[]{this};
    }


    /**
     * Returns the current status of the numerical display, indicating if it has been added or is displayed.
     *
     * @return An array of DisplayStatus representing the display's current status.
     */
    @Override
    public DisplayStatus[] getObjectsStatus() {
        if (!already_added) {
            already_added = true;
            return new DisplayStatus[]{DisplayStatus.ADD};
        }
        return new DisplayStatus[]{DisplayStatus.DISPLAYED};
    }

    /**
     * Updates the display based on the current number of lives, changing the display color according to the
     * value.
     * @param currentLives The current number of lives to display.
     */
    @Override
    public void notify(int currentLives) {
        Color color;
        text.setString(Integer.toString(currentLives));
        switch (currentLives) {
            case RED_ALERT:
                color = Color.red;
                break;
            case YELLOW_ALERT:
                color = Color.yellow;
                break;
            default:
                color = Color.green;
        }
        text.setColor(color);
    }


    /**
     * Determines whether this display should collide with another GameObject, based on tags.
     *
     * @param other The GameObject to check collision against.
     * @return false if the other object's tag is in the tagsToAvoid array, true otherwise.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        for (String tag : tagsToAvoid) {
            if (tag.equals(other.getTag())) {
                return false;
            }
        }

        return true;
    }
}
