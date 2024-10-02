package src.pepse.world;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import src.pepse.util.ColorSupplier;
import src.pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Generates and manages the terrain in the game world, using Perlin noise
 * to create natural-looking landscapes. Terrain blocks are created on demand
 * within a specified range of x-coordinates and are colored to represent ground.
 */
public class Terrain {

    /**
     * Proportion of the window's height that the initial ground height should occupy.
     */
    private static final double COEFFICIENT = 2.0 / 3;

    /**
     * Base color used for ground blocks.
     */
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);

    /**
     * Tag used to identify ground blocks.
     */
    public static final String TAG = "ground";

    /**
     * Scaling factor for noise, affecting the variation in terrain height.
     */
    private static final int NOISE_FACTOR = Block.SIZE * 7;

    /**
     * Dimensions of the window, used to determine terrain boundaries.
     */
    private final Vector2 windowDimensions;

    /**
     * Noise generator for terrain height variation.
     */
    private final NoiseGenerator noise;

    /**
     * Initial height of the ground at the left edge of the window.
     */
    private float groundHeightAtX0;

    /**
     * Cache mapping x-coordinates to calculated ground heights.
     */
    private HashMap<Integer, Integer> groundHeights;


    /**
     * Initializes a new Terrain object.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param seed             A seed for the noise generator to ensure consistent terrain generation.
     */
    public Terrain(Vector2 windowDimensions, int seed) {
        this.groundHeightAtX0 = (float) indexToStartBlock((int) (windowDimensions.y() * COEFFICIENT));
        this.windowDimensions = windowDimensions;
        noise = new NoiseGenerator(seed, (int) this.groundHeightAtX0);
        groundHeights = new HashMap<Integer, Integer>();
    }

    /**
     * Returns the height of the ground at a given x-coordinate.
     *
     * @param x The x-coordinate at which to determine ground height.
     * @return The ground height at the specified x-coordinate.
     */
    public float groundHeightAt(float x) {
        return groundHeights.get(indexToStartBlock((int) x));
    }


    /**
     * Determines the terrain height at a given x-coordinate with added Perlin noise for variation.
     *
     * @param x The x-coordinate for which to determine the height.
     * @return The height at the given x-coordinate, including noise for natural terrain variation.
     */
    private float groundHeightAtNoisy(float x) {
        float additive_noise = (float) noise.noise(x, NOISE_FACTOR);
        return groundHeightAtX0 + additive_noise;
    }

    /**
     * Generates terrain blocks within a specified range of x-coordinates.
     *
     * @param minX The minimum x-coordinate.
     * @param maxX The maximum x-coordinate.
     * @return A list of generated Block objects representing the terrain within the specified range.
     */
    public List<Block> createInRange(int minX, int maxX) {
        ArrayList<Block> arrayListOfBlocks = new ArrayList<Block>();
        for (int col = 0; col < numberOfBlocksPerLine(minX, maxX); col++) {
            int xCord = indexToStartBlock(minX) + Block.SIZE * col;
            int row = 0;
            int yCord = 0;
            do {
                RectangleRenderable rectangleRenderable =
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                yCord = getYCord(xCord, row);

                if (row == 0) {
                    groundHeights.put(xCord, yCord);
                }
                createSingleBlock(xCord, yCord, rectangleRenderable, arrayListOfBlocks);
                row++;
            } while (yCord <= windowDimensions.y() + Block.SIZE);

        }
        return arrayListOfBlocks;
    }

    /**
     * Returns the y-coordinate of a block at a given x-coordinate and row.
     *
     * @param xCord the x coordinate of the block
     * @param row the row of the block.
     * @return the y coordinate of the block
     */
    private int getYCord(int xCord, int row) {
        int yCord;
        yCord = (int) Math.floor(groundHeightAtNoisy(xCord) / Block.SIZE) *
                Block.SIZE + row * Block.SIZE;
        return yCord;
    }

    /**
     * Creates a single block at the specified x and y coordinates and adds it to the list of blocks.
     *
     * @param xCord               the x coordinate of the block
     * @param yCord               the y coordinate of the block
     * @param rectangleRenderable the rectangle renderable of the block
     * @param arrayListOfBlocks   the list of blocks
     */
    private static void createSingleBlock(int xCord, int yCord, RectangleRenderable rectangleRenderable,
                                          ArrayList<Block> arrayListOfBlocks) {
        Block ground = new Block(new Vector2(xCord, yCord),
                rectangleRenderable);
        ground.setTag(TAG);
        arrayListOfBlocks.add(ground);
    }

    /**
     * Calculates the number of blocks that fit within a line spanning a range of x-coordinates.
     *
     * @param minX The minimum x-coordinate of the range.
     * @param maxX The maximum x-coordinate of the range.
     * @return The number of blocks that can fit within the specified range.
     */
    private static int numberOfBlocksPerLine(int minX, int maxX) {
        return ((indexToStartBlock(maxX) - indexToStartBlock(minX)) / Block.SIZE) + 1;
    }

    /**
     * Determines the starting x-coordinate for a block based on a given x-coordinate.
     * Ensures that blocks align to a grid based on their size.
     *
     * @param curX The current x-coordinate.
     * @return The adjusted x-coordinate for the start of a block.
     */
    public static int indexToStartBlock(int curX) {
        return (int) Math.floor(((float) curX) / Block.SIZE) * Block.SIZE;
    }







}
