package src.bricker.lives_display;

import danogl.GameObject;
import danogl.gui.rendering.ImageRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import src.bricker.gameobjects.Heart;

/**
 * Manages the graphical display of lives in the game, represented by heart objects.
 */
public class GraphicDisplay implements LivesDisplay {
    /**
     * Array of heart objects used to represent lives graphically in the game.
     */
    private Heart[] hearts;

    /**
     * Maximum number of hearts (lives) that can be displayed.
     */
    private final int maxHearts;

    /**
     * Dimensions for each heart icon.
     */
    private final Vector2 heartDimensions;

    /**
     * Space between adjacent heart icons.
     */
    private final int spaceBetweenHearts;

    /**
     * ImageRenderable used for the visual representation of each heart icon.
     */
    private final ImageRenderable image;

    /**
     * Tags of other game objects that the hearts should not interact with.
     */
    private final String[] tagsToAvoid;

    /**
     * Array representing the display status of each heart icon to manage their addition or removal.
     */
    private DisplayStatus[] toRemove;

    /**
     * Current number of heart icons displayed, reflecting the number of lives.
     */
    private int amountOfHearts;

    /**
     * Constructs a new GraphicDisplay instance for managing heart icons as lives.
     *
     * @param maxHearts          The maximum number of hearts (lives) to display.
     * @param heartDimensions    The dimensions of each heart icon.
     * @param spaceBetweenHearts The space between adjacent heart icons.
     * @param image              The visual representation for the heart icons.
     * @param tagsToAvoid        Tags of objects that hearts should avoid colliding with.
     */
    public GraphicDisplay(int maxHearts, Vector2 heartDimensions, int spaceBetweenHearts,
                          ImageRenderable image, String[] tagsToAvoid) {
        hearts = new Heart[maxHearts];
        this.maxHearts = maxHearts;
        this.heartDimensions = heartDimensions;
        this.spaceBetweenHearts = spaceBetweenHearts;
        this.image = image;
        this.tagsToAvoid = tagsToAvoid;
        amountOfHearts = 0;

        //create all hearts (hidden)
        for (int i = 0; i < maxHearts; i++) {
            hearts[i] = new Heart(Vector2.ZERO, heartDimensions, image, tagsToAvoid);
        }
    }

    /**
     * Returns an array of heart GameObjects representing the lives.
     *
     * @return An array of heart GameObjects.
     */
    @Override
    public GameObject[] getGameObjects() {
        return hearts;
    }

    /**
     * Returns the current status of each heart object in the display.
     *
     * @return An array of DisplayStatus, indicating the status of each heart.
     */
    @Override
    public DisplayStatus[] getObjectsStatus() {
        DisplayStatus[] statuses = new DisplayStatus[maxHearts];
        for (int i = 0; i < maxHearts; i++) {
            statuses[i] = toRemove[i];

            if (toRemove[i] == DisplayStatus.REMOVE) {
                toRemove[i] = DisplayStatus.NONE;
            } else if (toRemove[i] == DisplayStatus.ADD) {
                toRemove[i] = DisplayStatus.DISPLAYED;
            }
        }
        return statuses;
    }

    /**
     * Updates the display based on the current number of lives.
     *
     * @param currentLives The current number of lives to display.
     */
    @Override
    public void notify(int currentLives) {
        toRemove = new DisplayStatus[maxHearts];
        for (int i = 0; i < toRemove.length; i++) {
            toRemove[i] = DisplayStatus.DISPLAYED;
        }

        if (currentLives < amountOfHearts) {
            // remove hearts.
            for (int i = amountOfHearts - 1; i >= currentLives; i--) {
                toRemove[i] = DisplayStatus.REMOVE;
            }
        } else if (currentLives > amountOfHearts) {
            // add hearts.
            for (int i = amountOfHearts; i < currentLives; i++) {
                toRemove[i] = DisplayStatus.ADD;
            }
        }

        amountOfHearts = currentLives;
    }

    /**
     * Sets the top-left corner position for the entire lives display.
     *
     * @param topLeftCorner The top-left corner position for the display.
     */
    @Override
    public void setTopLeftCorner(Vector2 topLeftCorner) {
        for (int i = 0; i < amountOfHearts; i++) {
            hearts[i].setTopLeftCorner(topLeftCorner.add(Vector2.RIGHT.mult(i * (heartDimensions.x()
                    + spaceBetweenHearts))));
        }
    }
}
