package src.pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.GameObjectPhysics;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import src.pepse.util.ColorSupplier;
import src.pepse.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Represents a Tree within the game environment. This class encapsulates the functionality
 * for creating and managing the tree's components, including its leaves, fruits, and stamp (trunk).
 * It also handles the visual effects such as wind movement on leaves.
 */

public class Tree {

    /**
     * Angle for leaf rotation to simulate wind effects.
     */
    private static final float LEAF_ROTATION_ANGLE = 90f;

    /**
     * Duration for leaves' transition during wind effects.
     */
    private static final int LEAVES_TRANSITION_TIME = 3;

    /**
     * Maximum rotation of leaves due to wind.
     */
    private static final int LEAVES_WIND_ROTATION = 30;

    /**
     * constant for the rotation of the leaves (half for every side).
     */
    private static final int HALF_LEAVES_WIND_ROTATION = LEAVES_WIND_ROTATION / 2;

    /**
     * Time for wind rotation transition on leaves.
     */
    private static final int LEAVES_WIND_ROTATION_TRANSITION_TIME = 7;

    /**
     * Maximum shrinking of leaves in wind simulation.
     */
    private static final int LEAVES_MAX_SHRINKING_IN_WIND = 3;

    /**
     * Bias probability for leaf placement.
     */
    private static final double LEAVES_BIAS_COIN = 0.4;

    /**
     * Bias probability for fruit generation.
     */
    private static final double FRUITS_BIAS_COIN = 0.9; // Corrected for clarity

    /**
     * Height of the tree's stamp.
     */
    private static final float HEIGHT_SIZE_OF_STAMP = 150f;

    /**
     * Width of the tree's stamp.
     */
    private static final float WIDTH_SIZE_OF_STAMP = 30f;

    /**
     * Grid size for leaf placement.
     */
    private static final int LEAVES_GRID_SIZE = 8;

    /**
     * Half of the grid number to calculate the place to put leaves and fruits.
     */
    private static final int HALF_NUMBER_OF_GRID_SIZE = LEAVES_GRID_SIZE / 2;

    /**
     * Size dimension for each leaf.
     */
    private static final float SIZE_OF_LEAF = HEIGHT_SIZE_OF_STAMP / LEAVES_GRID_SIZE;

    /**
     * Size dimension for each fruit.
     */
    private static final float SIZE_OF_FRUIT = SIZE_OF_LEAF - 5f;

    /**
     * space distance between fruits
     */
    private static final float SPACE_DISTANCE_BETWEEN_FRUITS = 2 * SIZE_OF_FRUIT;

    /**
     * Basic color for the tree's stamp.
     */
    private static final Color STAMP_BASIC_COLOR = new Color(100, 50, 20);

    /**
     * Basic color for the leaves.
     */
    private static final Color LEAF_BASIC_COLOR = new Color(50, 200, 30);

    /**
     * Minimum number of fruits per tree.
     */
    private static final int MIN_NUMBER_OF_FRUITS = 4;

    /**
     * Maximum number of fruits per tree.
     */
    private static final int MAX_NUMBER_OF_FRUITS = 6;

    /**
     * Array of GameObjects representing the leaves of the tree.
     */
    private GameObject[] leaves;

    /**
     * Array of GameObjects representing the fruits of the tree.
     */
    private GameObject[] fruits;

    /**
     * GameObject representing the tree's stamp (trunk).
     */
    private GameObject stamp;

    /**
     * Random generator for various random behaviors within the tree.
     */
    private static final Random rand = new Random();

    /**
     * the number of of the Fruits in the tree
     */
    private int numberOfFruits;

    /**
     * The bottom left corner position of the tree.
     */
    private final Vector2 bottomLeftCorner;

    /**
     * Top left corner of the grid for leaf placement.
     */
    private Vector2 gridTopLeftCorner;

    /**
     * Callback to add a jump tracking task.
     */
    private Consumer<Runnable> addJumpTracker;

    /**
     * Callback to remove GameObjects from the game.
     */
    private Consumer<GameObject> remover;

    /**
     * Callback to add GameObjects back to the game.
     */
    private Consumer<GameObject> adder;


    /**
     * Constructor for creating a Tree object within the game environment.
     * This tree consists of a stamp (trunk),
     * leaves, and fruits. The tree's physical properties and behaviors are initialized here,
     * including the placement
     * of leaves and fruits, and setting up the visual representation of the tree.
     *
     * @param bottomLeftCorner The bottom-left corner of the tree's position.
     * @param addJumpTracker   Callback for adding a jump tracker.
     * @param remover          Callback for removing objects from the game.
     * @param adder            Callback for adding objects back into the game.
     */
    public Tree(Vector2 bottomLeftCorner, Consumer<Runnable> addJumpTracker,
                Consumer<GameObject> remover, Consumer<GameObject> adder) {
        this.bottomLeftCorner = bottomLeftCorner;
        this.addJumpTracker = addJumpTracker;
        this.remover = remover;
        this.adder = adder;
        createdStamp();
        calculateLeavesGridTopLeftCorner();
        createdLeaves();
        createdFruits();
    }

    /**
     * Calculates the top-left corner for the grid layout of leaves based on the stamp's position.
     * This position is used as a starting point for placing leaves on the tree.
     */
    private void calculateLeavesGridTopLeftCorner() {
        gridTopLeftCorner =
                stamp.getTopLeftCorner().add(Vector2.UP.mult(HALF_NUMBER_OF_GRID_SIZE *
                        SIZE_OF_LEAF));
        gridTopLeftCorner = gridTopLeftCorner.add(Vector2.LEFT.mult(HALF_NUMBER_OF_GRID_SIZE *
                SIZE_OF_LEAF));
    }


    /**
     * Collects and returns all tree components (leaves, fruits, and stamp) along with their corresponding
     * layer information. This method is useful for managing game object rendering layers and interactions
     * within the game environment. The stamp is added to the static objects layer, leaves to the background
     * layer, and fruits to the default layer,
     * indicating their relative positions and interactions in the game.
     *
     * @return An ArrayList of Pair objects, each containing a GameObject (a tree component) and an Integer
     * representing the layer the GameObject belongs to. The layers are defined in the Layer class.
     */
    public ArrayList<Pair<GameObject, Integer>> getAllObjects() {
        ArrayList<Pair<GameObject, Integer>> pairs = new ArrayList<Pair<GameObject, Integer>>();

        pairs.add(new Pair<GameObject, Integer>(stamp, Layer.STATIC_OBJECTS));
        for (GameObject leaf : leaves) {
            pairs.add(new Pair<GameObject, Integer>(leaf, Layer.BACKGROUND));
        }
        for (GameObject fruit : fruits) {
            pairs.add(new Pair<GameObject, Integer>(fruit, Layer.DEFAULT));
        }

        return pairs;
    }

    /**
     * Initializes the leaves of the tree. Leaves are placed on a grid, and each leaf's position is
     * calculated based on the grid's top-left corner. Wind movement effects are also applied to simulate
     * natural leaf behavior.
     */
    private void createdLeaves() {
        ArrayList<GameObject> leavesList = new ArrayList<GameObject>();
        for (int row = 0; row < LEAVES_GRID_SIZE; row++) {
            for (int col = 0; col < LEAVES_GRID_SIZE; col++) {
                if (bernouli(LEAVES_BIAS_COIN)) {
                    GameObject leaf = createSingleLeaf();
                    leavesList.add(leaf);
                    setLeafLocation(row, col, leaf);
                    leafRotationByJump(leaf);
                    leafWindMovemnent(leaf);
                }
            }
        }
        leaves = leavesList.toArray(GameObject[]::new);
    }

    /**
     * Simulates wind movement on a leaf by applying rotation and shrinking effects. These visual transitions
     * make the leaves appear to flutter in the wind.
     *
     * @param leaf The leaf GameObject to apply wind movement effects to.
     */
    private void leafWindMovemnent(GameObject leaf) {
        new ScheduledTask(leaf, rand.nextFloat(), false, new Runnable() {
            @Override
            public void run() {
                createLeafRotationTransition(leaf);
                createLeafShrinkingTransition(leaf);
            }
        });
    }

    /**
     * Creates a transition effect that simulates the shrinking of a leaf due to wind. This effect alternates
     * between the leaf's original size and a slightly smaller size to mimic natural movement.
     *
     * @param leaf The leaf GameObject to apply the shrinking effect to.
     */
    private static void createLeafShrinkingTransition(GameObject leaf) {
        float initialWidth = leaf.getDimensions().x();
        float height = leaf.getDimensions().y();
        Transition<Float> widthTransition = new Transition<Float>(
                leaf,
                (Float width) -> leaf.setDimensions(new Vector2(width, height)),
                initialWidth,
                initialWidth - LEAVES_MAX_SHRINKING_IN_WIND,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                LEAVES_WIND_ROTATION_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
    }

    /**
     * Creates a rotation transition for a leaf to simulate the effect of wind. This transition makes the leaf
     * rotate back and forth around its original angle.
     *
     * @param leaf The leaf GameObject to apply the rotation transition to.
     */
    private static void createLeafRotationTransition(GameObject leaf) {
        float initial_angle = leaf.renderer().getRenderableAngle();
        Transition<Float> angleTransition = new Transition<Float>(
                leaf,
                (Float angle) -> leaf.renderer().setRenderableAngle(angle),
                initial_angle - HALF_LEAVES_WIND_ROTATION,
                initial_angle + HALF_LEAVES_WIND_ROTATION,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                LEAVES_WIND_ROTATION_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
    }

    /**
     * Sets the location of a leaf within the grid.
     * The leaf's position is calculated based on its row and column
     * in the grid, relative to the grid's top-left corner.
     *
     * @param row  The row in the grid where the leaf is placed.
     * @param col  The column in the grid where the leaf is placed.
     * @param leaf The leaf GameObject to position.
     */
    private void setLeafLocation(int row, int col, GameObject leaf) {
        Vector2 leafTopLeftCorner = gridTopLeftCorner.add(Vector2.DOWN.mult(row * SIZE_OF_LEAF));
        leafTopLeftCorner = leafTopLeftCorner.add(Vector2.RIGHT.mult(col * SIZE_OF_LEAF));
        leaf.setTopLeftCorner(leafTopLeftCorner);
    }

    /**
     * Creates a single leaf GameObject with a randomized color. The leaf is rendered as a rectangle and sized
     * according to the predefined size of a leaf.
     *
     * @return A new leaf GameObject.
     */
    private static GameObject createSingleLeaf() {
        Color leafColor = ColorSupplier.approximateColor(LEAF_BASIC_COLOR);
        RectangleRenderable leafRectangleRenderable = new RectangleRenderable(leafColor);
        Vector2 sizeOfLeaf = new Vector2(SIZE_OF_LEAF, SIZE_OF_LEAF);
        GameObject leaf = new GameObject(Vector2.ZERO, sizeOfLeaf, leafRectangleRenderable);
        return leaf;
    }

    /**
     * Applies a rotation effect to a leaf whenever the Avatar jumps. This effect is part of the interactive
     * elements in the game, providing a dynamic environment.
     *
     * @param leaf The leaf GameObject to apply the rotation effect to.
     */
    private void leafRotationByJump(GameObject leaf) {
        addJumpTracker.accept(new Runnable() {
            @Override
            public void run() {
                float initial_angle = leaf.renderer().getRenderableAngle();
                Transition<Float> transition = new Transition<Float>(
                        leaf, (Float angle) -> leaf.renderer().setRenderableAngle(angle),
                        initial_angle, initial_angle + LEAF_ROTATION_ANGLE,
                        Transition.LINEAR_INTERPOLATOR_FLOAT, LEAVES_TRANSITION_TIME,
                        Transition.TransitionType.TRANSITION_ONCE, null);
            }
        });
    }

    /**
     * Initializes the fruits of the tree. Fruits are randomly placed within the tree's area,
     * ensuring they do not
     * overlap with each other. Each fruit is capable of interacting with the Avatar.
     */
    private void createdFruits() {
        numberOfFruits = rand.nextInt(MAX_NUMBER_OF_FRUITS - MIN_NUMBER_OF_FRUITS) +
                MIN_NUMBER_OF_FRUITS;
        fruits = new Fruit[numberOfFruits];

        for (int count = 0; count < numberOfFruits; count++) {
            Vector2 sizeOfFruit = new Vector2(SIZE_OF_FRUIT, SIZE_OF_FRUIT);
            // randomize non-overlapping location.
            Vector2 center = Vector2.ZERO;
            while (true) {
                float x = rand.nextFloat() * LEAVES_GRID_SIZE;
                float y = rand.nextFloat() * LEAVES_GRID_SIZE;
                center = gridTopLeftCorner.add(new Vector2(x, y).mult(SIZE_OF_FRUIT));

                boolean valid = true;
                for (GameObject other : fruits) {
                    if (other != null && distance(other.getCenter(), center) <=
                            SPACE_DISTANCE_BETWEEN_FRUITS) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    break;
                }
            }
            fruits[count] = new Fruit(Vector2.ZERO, sizeOfFruit, addJumpTracker, remover, adder);
            fruits[count].setCenter(center);
        }
    }

    /**
     * Creates the stamp (trunk) of the tree.
     * The stamp is visualized using a rectangle with a predefined color
     * and size, and its position is set based on the bottom-left corner of the tree.
     */
    private void createdStamp() {
        Color stampColor = ColorSupplier.approximateColor(STAMP_BASIC_COLOR);
        RectangleRenderable stampRectangleRenderable = new RectangleRenderable(stampColor);
        Vector2 sizeOfStamp = new Vector2(WIDTH_SIZE_OF_STAMP, HEIGHT_SIZE_OF_STAMP);
        stamp = new GameObject(Vector2.ZERO, sizeOfStamp, stampRectangleRenderable);
        stamp.setTopLeftCorner(bottomLeftCorner.add(Vector2.UP.mult(HEIGHT_SIZE_OF_STAMP)));
        stamp.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        stamp.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);

        addJumpTracker.accept(new Runnable() {
            @Override
            public void run() {
                Color stampColor = ColorSupplier.approximateColor(STAMP_BASIC_COLOR);
                RectangleRenderable stampRectangleRenderable = new RectangleRenderable(stampColor);
                stamp.renderer().setRenderable(stampRectangleRenderable);
            }
        });
    }

    /**
     * Performs a Bernoulli trial to determine the placement of a leaf or fruit based on a given probability.
     *
     * @param alpha The probability of success.
     * @return True if the trial is successful; False otherwise.
     */
    private static boolean bernouli(double alpha) {
        return (rand.nextDouble() > alpha);
    }

    /**
     * Calculates the distance between two points. This function is used to ensure fruits are not placed too
     * close to each other.
     *
     * @param a The first point.
     * @param b The second point.
     * @return The Euclidean distance between points a and b.
     */
    private static double distance(Vector2 a, Vector2 b) {
        Vector2 dist = a.subtract(b);
        return Math.sqrt(dist.x() * dist.x() + dist.y() * dist.y());
    }
}
