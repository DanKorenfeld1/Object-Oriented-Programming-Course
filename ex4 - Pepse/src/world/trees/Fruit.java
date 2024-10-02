package src.pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import src.pepse.world.Avatar;

import java.awt.*;
import java.util.function.Consumer;

/**
 * Represents a Fruit object in the game which can interact with the Avatar.
 * It changes colors, provides energy to the Avatar upon collision, and
 * implements a mechanism to reappear after a certain period.
 */
public class Fruit extends GameObject {

    /**
     * Tag identifier for Fruit objects.
     */
    private static final String TAG = "fruit";

    /**
     * Energy boost value provided to the Avatar upon collision.
     */
    private static final int ENERGY_BOOST = 10;

    /**
     * Time delay before the Fruit reappears after being collected.
     */
    private static final int WAIT_TIME = 30;

    /**
     * Indicator to not repeat the scheduled task.
     */
    private static final boolean DONT_REPEAT = false;

    /**
     * Array of colors the Fruit can change through.
     */
    private static final Color[] COLORS = {Color.RED, Color.YELLOW, Color.ORANGE};

    /**
     * Initial the color counter of the fruits.
     */
    private static final int INITIAL_COLOR_COUNTER = 0;

    /**
     * Callback to add a task for tracking jumps.
     */
    private Consumer<Runnable> addJumpTracker;

    /**
     * Callback to remove this Fruit object from the game.
     */
    private Consumer<GameObject> remover;

    /**
     * Callback to add this Fruit object back to the game.
     */
    private Consumer<GameObject> adder;

    /**
     * Placeholder GameObject for scheduled tasks after Fruit removal.
     */
    private GameObject leach;

    /**
     * Counter to keep track of the current color of the Fruit.
     */
    private int color_counter;


    /**
     * Initializes a new Fruit object.
     *
     * @param topLeftCorner  The top left corner position of the Fruit.
     * @param dimensions     The size dimensions of the Fruit.
     * @param addJumpTracker Callback to add a jump tracking task.
     * @param remover        Callback to remove this Fruit from the game.
     * @param adder          Callback to add this Fruit back to the game.
     */
    public Fruit(Vector2 topLeftCorner, Vector2 dimensions,
                 Consumer<Runnable> addJumpTracker,
                 Consumer<GameObject> remover, Consumer<GameObject> adder) {
        super(topLeftCorner, dimensions, null);
        color_counter = INITIAL_COLOR_COUNTER;
        OvalRenderable fruitOvalRenderable = new OvalRenderable(COLORS[color_counter]);
        renderer().setRenderable(fruitOvalRenderable);
        setTag(TAG);
        this.addJumpTracker = addJumpTracker;
        this.remover = remover;
        this.adder = adder;

        // We add the leach object to the game manager so the scheduled task would have an object
        // to "leach" upon it's update method after this fruit is removed.
        leach = new GameObject(Vector2.ZERO, Vector2.ZERO, null);
        adder.accept(leach);
        addJumpTracker.accept(new Runnable() {
            @Override
            public void run() {
                cyclicNextColor();
                OvalRenderable fruitOvalRenderable = new OvalRenderable(COLORS[color_counter]);
                renderer().setRenderable(fruitOvalRenderable);
            }
        });
    }

    /**
     * Advances the color counter to the next color in a cyclic manner.
     * This method ensures that the Fruit object cycles through a predefined
     * set of colors, changing its appearance in the game.
     */
    private void cyclicNextColor() {
        color_counter = (color_counter + 1) % COLORS.length;
    }

    /**
     * Handles the behavior of the Fruit upon collision with other game objects.
     * Specifically, it provides an energy boost to the Avatar and schedules
     * its re-addition to the game after a defined period.
     *
     * @param other     The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     * @note there is no callback strategy implementation for onCollisionEnter as opposed to
     * update & it's addComponent so inheritance was necessary.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        if (other.getTag().equals(Avatar.TAG) && other instanceof Avatar) {
            Avatar avi = (Avatar) other;
            avi.addEnergy(ENERGY_BOOST);
            remover.accept(this);
            illBeBack();
        }
    }

    /**
     * Schedules the re-addition of this Fruit to the game after a specified wait time.
     */
    private void illBeBack() {
        ScheduledTask task = new ScheduledTask(leach, WAIT_TIME, DONT_REPEAT, () -> adder.accept(this));
    }
}
