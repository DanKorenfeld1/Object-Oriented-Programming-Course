package src.pepse.world.trees;

import danogl.GameObject;
import danogl.util.Vector2;
import src.pepse.world.Block;
import src.pepse.world.Terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Manages the creation and placement of flora, specifically trees,
 * within a designated range in the game world.
 * This class is responsible for generating trees at various locations based on a defined sparsity and
 * probability, creating a more natural and dynamic environment.
 */
public class Flora {
    /**
     * The probability bias for generating a tree.
     * Represents the chance that a space will be left empty rather than being filled with a tree to control
     * the density of tree placement.
     */
    private static final double TREES_BIAS_COIN = 1 - 0.1;

    /**
     * Defines the sparsity of the forest by specifying the minimum distance between trees.
     * This distance impacts how densely populated areas of the game world will be with trees.
     */
    private static final int FOREST_SPARSETY = Block.SIZE * 2;

    /**
     * A Random instance used for generating random boolean values to decide whether a tree should be placed
     * based on TREES_BIAS_COIN.
     */
    private final Random random = new Random();

    /**
     * A function to obtain the ground height at a specific x-coordinate, used to place trees at the correct
     * vertical position.
     */
    private Function<Float, Float> groundHeightAt;

    /**
     * A consumer that allows the flora to trigger actions related to the game world,
     * such as adding physics interactions.
     */
    private Consumer<Runnable> addJumpTracker;

    /**
     * A consumer for removing GameObjects from the game world,
     * used when trees need to be dynamically removed.
     */
    private Consumer<GameObject> remover;

    /**
     * A consumer for adding GameObjects to the game world,
     * enabling the placement of trees within the environment.
     */
    private Consumer<GameObject> adder;


    /**
     * Initializes a new instance of the Flora class with the necessary dependencies for tree creation
     * and management.
     *
     * @param groundHeightAt A function to determine the ground height at a given x-coordinate.
     * @param addJumpTracker A consumer to register actions or events in the game world.
     * @param remover        A consumer for removing GameObjects from the game.
     * @param adder          A consumer for adding GameObjects to the game.
     */
    public Flora(Function<Float, Float> groundHeightAt,
                 Consumer<Runnable> addJumpTracker,
                 Consumer<GameObject> remover, Consumer<GameObject> adder) {
        this.groundHeightAt = groundHeightAt;
        this.addJumpTracker = addJumpTracker;
        this.remover = remover;
        this.adder = adder;
    }

    /**
     * Generates and returns a list of trees within a specified range.
     * Trees are placed based on a defined probability and sparsity, ensuring a natural distribution.
     *
     * @param minX The minimum x-coordinate to start generating trees.
     * @param maxX The maximum x-coordinate to end generating trees.
     * @return A list of Tree objects that have been placed within the specified range.
     */

    public List<Tree> createInRange(int minX, int maxX) {
        ArrayList<Tree> trees = new ArrayList<Tree>();

        // start from Terrain.indexToStartBlock(minX) + Block.SIZE so no tree would
        // be planted in the intial location of our avatar.
        for (int x = Terrain.indexToStartBlock(minX) + Block.SIZE;
             x <= Terrain.indexToStartBlock(maxX); x += Block.SIZE) {
            if (bernouli(TREES_BIAS_COIN)) {
                trees.add(new Tree(new Vector2(x, groundHeightAt.apply((float) x)),
                        addJumpTracker, remover, adder));
                x += FOREST_SPARSETY;
            }
        }

        return trees;
    }

    /**
     * Generates a boolean value based on a Bernoulli distribution,
     * determining whether a tree should be placed.
     *
     * @param alpha The probability of generating a true value, influencing tree placement.
     * @return A boolean indicating whether a tree should be placed (true) or not (false).
     */

    private boolean bernouli(double alpha) {
        return (random.nextDouble() > alpha);
    }
}
