package src.pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.Component;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import src.pepse.util.Pair;
import src.pepse.world.Avatar;
import src.pepse.world.Block;
import src.pepse.world.Sky;
import src.pepse.world.Terrain;
import src.pepse.world.daynight.Night;
import src.pepse.world.daynight.Sun;
import src.pepse.world.daynight.SunHalo;
import src.pepse.world.trees.Flora;
import src.pepse.world.trees.Tree;

import java.util.List;

/**
 * Manages the overall game logic, initialization, and setup for the PEPSE game environment. This includes
 * creating the game world's terrain, sky, day-night cycle effects, the player's avatar, an energy display,
 * and a dynamically generated forest. The game manager orchestrates the interactions between these components
 * to provide a cohesive gameplay experience.
 */
public class PepseGameManager extends GameManager {
    /**
     * The length of the day-night cycle in the game, affecting the transition duration
     * between day and night states.
     */
    private static final int DAY_CYCLE_LENGTH = 30;

    /**
     * The format string for displaying the avatar's energy level as a percentage.
     */
    private static final String PERCENT_SYMBOL_FORMAT = "%";

    /**
     * The size of the text object used for displaying the avatar's energy level.
     */
    private static final int SIZE_TEXT_ENERGY = 50;

    /**
     * The minimum x-coordinate used when generating the game world elements,
     * such as terrain and forest, to ensure they span from the beginning of the window.
     */
    private static final int MIN_X_WINDOW = 0;

    /**
     * a scalar for converting between units, milliseconds to seconds and vice versa.
     */
    private static final int MILLISECONDS_TO_SECONDS = 100;

    /**
     * Represents the player's avatar in the game, which the player controls
     * to navigate the game world and interact with the environment.
     */
    private Avatar avi;

    /**
     * Represents the world's terrain.
     */
    private Terrain terrain;

    /**
     * Initializes the game by setting up the game window and creating game objects including
     * the sky, terrain, night overlay, sun, sun halo, the player's avatar, an energy display,
     * and the forest.
     *
     * @param imageReader      For reading images from resources.
     * @param soundReader      For reading sound files from resources.
     * @param inputListener    For handling user inputs.
     * @param windowController For managing the game window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        createSky(windowController);
        terrain = createTerrain(windowController);
        createNight(windowController);
        GameObject sun = createSun(windowController);
        createSunHalo(sun);
        Avatar avi = createAvatar(imageReader, inputListener, terrain);
        createEnergyDisplay(avi);
        createForest(windowController, terrain);
    }

    /**
     * Creates and initializes the player's avatar and places it on the terrain.
     *
     * @param imageReader   For reading avatar images from resources.
     * @param inputListener For handling avatar controls.
     * @param terrain       The game's terrain for initial placement of the avatar.
     * @return The created Avatar object.
     */
    private Avatar createAvatar(ImageReader imageReader, UserInputListener inputListener, Terrain terrain) {
        avi = new Avatar(Vector2.ZERO, inputListener, imageReader);
        avi.setTopLeftCorner(new Vector2(MIN_X_WINDOW,
                terrain.groundHeightAt(MIN_X_WINDOW) - Avatar.HEIGHT - Block.SIZE));
        gameObjects().addGameObject(avi);
        return avi;
    }

    /**
     * Creates a sun halo effect around the sun.
     *
     * @param sun The sun GameObject to which the halo effect is applied.
     */
    private void createSunHalo(GameObject sun) {
        GameObject halo = SunHalo.create(sun);
        gameObjects().addGameObject(halo, Layer.BACKGROUND);
    }

    /**
     * Creates the sun and initiates the day-night cycle.
     *
     * @param windowController For managing the game window and obtaining dimensions.
     * @return The created sun GameObject.
     */
    private GameObject createSun(WindowController windowController) {
        Sun.setHorizonHeightRetriever(terrain::groundHeightAt);
        GameObject sun = Sun.create(windowController.getWindowDimensions(), DAY_CYCLE_LENGTH);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);
        return sun;
    }

    /**
     * Creates the night overlay for the day-night cycle.
     *
     * @param windowController For managing the game window and obtaining dimensions.
     */
    private void createNight(WindowController windowController) {
        GameObject night = Night.create(windowController.getWindowDimensions(), DAY_CYCLE_LENGTH);
        gameObjects().addGameObject(night, Layer.UI);
    }

    /**
     * Generates the terrain of the game world.
     *
     * @param windowController For managing the game window and obtaining dimensions.
     * @return The created Terrain object.
     */
    private Terrain createTerrain(WindowController windowController) {
        Terrain terrain = new Terrain(windowController.getWindowDimensions(), getRandomSeed());
        List<Block> blocks = terrain.createInRange(MIN_X_WINDOW,
                (int) Math.ceil(windowController.getWindowDimensions().x()));
        for (Block block : blocks) {
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
        }
        return terrain;
    }

    /**
     * get a new random seed based on the current time.
     *
     * @return seed for random number generator.
     */
    private int getRandomSeed() {
        return (int)(System.currentTimeMillis() / MILLISECONDS_TO_SECONDS);
    }

    /**
     * Creates the sky background.
     *
     * @param windowController For managing the game window and obtaining dimensions.
     */
    private void createSky(WindowController windowController) {
        GameObject sky = Sky.create(windowController.getWindowDimensions());
        gameObjects().addGameObject(sky, Layer.BACKGROUND);
    }

    /**
     * Creates a display for the avatar's energy level.
     *
     * @param avi The player's avatar whose energy level is displayed.
     */
    private void createEnergyDisplay(Avatar avi) {
        TextRenderable textRenderable = new TextRenderable(Float.toString(avi.getEnergy()));
        GameObject text = new GameObject(Vector2.ZERO, new Vector2(SIZE_TEXT_ENERGY, SIZE_TEXT_ENERGY),
                textRenderable);
        text.addComponent(new Component() {
            @Override
            public void update(float deltaTime) {
                textRenderable.setString(Integer.toString((int) Math.floor(avi.getEnergy())) +
                        PERCENT_SYMBOL_FORMAT);
            }
        });
        gameObjects().addGameObject(text, Layer.BACKGROUND);
    }

    /**
     * Generates the forest environment within the game world.
     *
     * @param windowController For managing the game window and obtaining dimensions.
     * @param terrain          The game's terrain used for tree placement.
     */
    private void createForest(WindowController windowController, Terrain terrain) {
        Flora flora = new Flora(terrain::groundHeightAt,
                avi::addJumpObserver, this::removeObject, this::addObject);
        List<Tree> trees = flora.createInRange(MIN_X_WINDOW,
                (int) Math.ceil(windowController.getWindowDimensions().x()));
        for (Tree tree : trees) {
            for (Pair<GameObject, Integer> pair : tree.getAllObjects()) {
                gameObjects().addGameObject(pair.getFirst(), pair.getSecond());
            }
        }
    }

    /**
     * Helper method to remove a GameObject from the game.
     *
     * @param objectToRemove The GameObject to remove.
     */
    private void removeObject(GameObject objectToRemove) {
        gameObjects().removeGameObject(objectToRemove);
    }

    /**
     * Helper method to add a GameObject to the game.
     *
     * @param objectToAdd The GameObject to add.
     */
    private void addObject(GameObject objectToAdd) {
        gameObjects().addGameObject(objectToAdd);
    }

    /**
     * The entry point of the game. Creates an instance of PepseGameManager and starts the game.
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }

}
