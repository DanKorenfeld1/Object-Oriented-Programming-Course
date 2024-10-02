package src.pepse.world;

import java.util.ArrayList;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import src.pepse.util.Pair;

import java.awt.event.KeyEvent;

/**
 * Avatar class represents the player-controlled character in the game. It is capable of moving
 * left, right, jumping, and responding to physics such as gravity. The Avatar's visual appearance
 * and animations change based on its actions like idle, walking, or jumping. Energy mechanics are
 * implemented to limit continuous actions like jumping, introducing a strategic element to the gameplay.
 */
public class Avatar extends GameObject {
    /**
     * The tag used for identifying the Avatar object within the game.
     */
    public static final String TAG = "avatar";

    /**
     * The fixed height of the Avatar.
     */
    public static final int HEIGHT = 50;

    /**
     * The fixed width of the Avatar.
     */
    private static final int WIDTH = 30;

    /**
     * The walking speed of the Avatar in units per second.
     */
    private static final float WALK_SPEED = 400f;

    /**
     * The upward force applied when the Avatar jumps, affecting its vertical speed.
     */
    private static final float AIRSPEED_VELOCITY = 650f;

    /**
     * The gravitational force applied to the Avatar, affecting its vertical movement.
     */
    private static final float GRAVITY = 600f;

    /**
     * The initial energy level of the Avatar at the start.
     */
    private static final float INIT_ENERGY = 0f;

    /**
     * The maximum energy level the Avatar can have.
     */
    private static final float MAX_ENERGY = 100f;

    /**
     * The time between frame changes in animations, in seconds.
     */
    private static final float TIME_BETWEEN_CLIPS = 0.25f;

    /**
     * The number of images composing the idle animation sequence.
     */
    private static final int NUMBER_OF_IDLE_IMAGES = 4;

    /**
     * The number of images composing the jump animation sequence.
     */
    private static final int NUMBER_OF_JUMP_IMAGES = 4;

    /**
     * The number of images composing the walk animation sequence.
     */
    private static final int NUMBER_OF_WALK_IMAGES = 6;

    /**
     * Placeholder for formatting the file paths of animation images.
     */
    private static final String PLACEHOLDER = "<id>";

    /**
     * The format string for the file path of idle animation images.
     */
    private static final String IDLE_IMAGE_NAME_FORMAT = "src/assets/idle_" + PLACEHOLDER + ".png";

    /**
     * The format string for the file path of jump animation images.
     */
    private static final String JUMP_IMAGE_NAME_FORMAT = "src/assets/jump_" + PLACEHOLDER + ".png";

    /**
     * The format string for the file path of walk animation images.
     */
    private static final String WALK_IMAGE_NAME_FORMAT = "src/assets/run_" + PLACEHOLDER + ".png";

    /**
     * The energy penalty for performing a jump action.
     */
    private static final int JUMP_PENALTY = 10;

    /**
     * The energy penalty for walking, applied per update cycle.
     */
    private static final float WALK_PENALTY = 0.5f;

    /**
     * The energy credit for being idle, applied per update cycle.
     */
    private static final int IDLE_PENALTY = -1;

    /**
     * The energy change for falling, applied per update cycle.
     */
    private static final int FALL_PENALTY = 0;

    /**
     * The minimal energy level the Avatar can have.
     */
    private static final int MINIMAL_ENERGY = 0;

    /**
     * The value indicating that the Avatar is not moving horizontally.
     */
    private static final int NOT_MOVING_IN_X = 0;

    /**
     * Listener for user inputs, used to determine Avatar's actions based on key presses.
     */
    private final UserInputListener userInputListener;

    /**
     * The renderable object for the Avatar's idle animation.
     */
    private final AnimationRenderable idleAnimation;

    /**
     * The renderable object for the Avatar's jump animation.
     */
    private final AnimationRenderable jumpAnimation;

    /**
     * The renderable object for the Avatar's run animation.
     */
    private final AnimationRenderable runAnimation;

    /**
     * The current energy level of the Avatar, affecting its ability to jump.
     */
    private float energy;

    /**
     * A flag indicating whether the Avatar is grounded, affecting its ability to jump.
     */
    private boolean isGrounded;

    /**
     * A list of observers to be notified when the Avatar jumps.
     */
    private ArrayList<Runnable> jumpObservers;


    /**
     * Constructor for the Avatar class. Initializes the Avatar with animations, physics, and input handling.
     *
     * @param pos           The initial position of the Avatar.
     * @param inputListener The UserInputListener that handles user inputs.
     * @param imageReader   The ImageReader used to load the Avatar's animations.
     */
    public Avatar(Vector2 pos, UserInputListener inputListener, ImageReader imageReader) {
        super(pos.add(Vector2.UP.mult(HEIGHT)), new Vector2(WIDTH, HEIGHT), null);
        userInputListener = inputListener;

        simulatePhysics();

        idleAnimation = getIdleAnimation(imageReader);
        jumpAnimation = getJumpAnimation(imageReader);
        runAnimation = getRunAnimation(imageReader);

        this.renderer().setRenderable(runAnimation);
        isGrounded = false;
        energy = INIT_ENERGY;
        jumpObservers = new ArrayList<Runnable>();
        setTag(TAG);
    }

    /**
     * Updates the Avatar's state on each frame, including handling user inputs for movement
     * and managing the Avatar's energy and animations based on its actions.
     *
     * @param deltaTime The time elapsed since the last frame, used for frame-independent movement.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 movement = Vector2.ZERO;
        Vector2 vertical = Vector2.ZERO;

        if (userInputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            movement = movement.add(Vector2.LEFT);
        }
        if (userInputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            movement = movement.add(Vector2.RIGHT);
        }
        if (userInputListener.isKeyPressed(KeyEvent.VK_SPACE) && isGrounded) {
            vertical = Vector2.UP.mult(AIRSPEED_VELOCITY);
        }

        AvatarState state = determineState(movement, vertical);
        float penalty = getPenalty(state);

        Pair<AvatarState, Float> pair = handleMovement(penalty, movement, vertical, state);

        addEnergy(-pair.getSecond());
        energyClipping();
        chooseAnimation(pair.getFirst(), movement);
    }


    /**
     * Registers a new observer to be notified when the Avatar jumps.
     *
     * @param jumpObserver A runnable that will be executed whenever the Avatar jumps.
     */
    public void addJumpObserver(Runnable jumpObserver) {
        jumpObservers.add(jumpObserver);
    }

    /**
     * Retrieves the current energy level of the Avatar.
     *
     * @return The current energy level as a float.
     */
    public float getEnergy() {
        return energy;
    }

    /**
     * Adds a specified amount of energy to the Avatar's current energy level,
     * ensuring that the energy level does not exceed its maximum value.
     *
     * @param addEnergy The amount of energy to add.
     */
    public void addEnergy(float addEnergy) {
        energy += addEnergy;
        energyClipping();
    }

    /**
     * Handles collisions between the Avatar and other objects in the game world,
     * specifically to check if the Avatar has landed on the ground to allow jumping again.
     *
     * @param other     The GameObject that the Avatar collided with.
     * @param collision Details about the collision event.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        if (other.getTag().equals(Terrain.TAG)) {
            isGrounded = true;
        }
    }

    /**
     * Manages the Avatar's movement and energy consumption based on user input and current state.
     *
     * @param penalty  The energy penalty for the current action.
     * @param horizontal The desired movement direction and magnitude.
     * @param vertical The vertical force to be applied for jumping.
     * @param state    The current state of the Avatar.
     * @return A Pair containing the updated state and energy penalty.
     */
    private Pair<AvatarState, Float> handleMovement(float penalty, Vector2 horizontal,
                                                    Vector2 vertical, AvatarState state) {
        if (energy - penalty >= MINIMAL_ENERGY &&
                (state == AvatarState.JUMP || state == AvatarState.WALKING)) {
            if (state == AvatarState.JUMP) {
                setVelocity(horizontal.mult(WALK_SPEED).add(vertical));
                jump();
            } else {

                transform().setVelocityX(horizontal.mult(WALK_SPEED).x());
            }
        } else {
            if (isGrounded) {
                state = AvatarState.IDLE;
                penalty = IDLE_PENALTY;
            } else {
                state = AvatarState.FALLING;
                penalty = FALL_PENALTY;
            }

            idle(state);
        }
        return new Pair<AvatarState, Float>(state, penalty);
    }

    /**
     * zeros horizontal movement of the avatar if the state is IDLE.
     *
     * @param state the current state of the avater.
     */
    private void idle(AvatarState state) {
        if (state == AvatarState.IDLE) {
            transform().setVelocityX(Vector2.ZERO.x());
        }
    }

    /**
     * Sets up the physics for the Avatar, including gravity and prevention of intersections.
     */
    private void simulatePhysics() {
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
    }

    /**
     * Creates an animation renderable for the Avatar based on the provided parameters.
     *
     * @param imageReader      The image reader to load animation frames.
     * @param imagesNameFormat The format string for the names of the image files.
     * @param numberOfImages   The number of images to include in the animation.
     * @return An AnimationRenderable containing the animation frames.
     */
    private static AnimationRenderable getAnimation(
            ImageReader imageReader, String imagesNameFormat, int numberOfImages) {
        Renderable[] images = new Renderable[numberOfImages];

        for (int i = 0; i < numberOfImages; i++) {
            images[i] = imageReader.readImage(
                    imagesNameFormat.replace(PLACEHOLDER, Integer.toString(i)),
                    false
            );
        }
        AnimationRenderable animationRenderable = new AnimationRenderable(images, TIME_BETWEEN_CLIPS);
        return animationRenderable;
    }

    /**
     * Generates the jump animation for the Avatar.
     *
     * @param imageReader The image reader to load animation frames.
     * @return An AnimationRenderable for the jump animation.
     */
    private static AnimationRenderable getJumpAnimation(ImageReader imageReader) {
        return getAnimation(imageReader, JUMP_IMAGE_NAME_FORMAT, NUMBER_OF_JUMP_IMAGES);
    }

    /**
     * Ensures the Avatar's energy does not exceed the maximum allowed value.
     */
    private void energyClipping() {
        if (energy >= MAX_ENERGY) {
            energy = MAX_ENERGY;
        }
    }

    /**
     * Generates the run animation for the Avatar.
     *
     * @param imageReader The image reader to load animation frames.
     * @return An AnimationRenderable for the run animation.
     */
    private static AnimationRenderable getRunAnimation(ImageReader imageReader) {
        return getAnimation(imageReader, WALK_IMAGE_NAME_FORMAT, NUMBER_OF_WALK_IMAGES);
    }

    /**
     * Generates the idle animation for the Avatar.
     *
     * @param imageReader The image reader to load animation frames.
     * @return An AnimationRenderable for the idle animation.
     */
    private static AnimationRenderable getIdleAnimation(ImageReader imageReader) {
        return getAnimation(imageReader, IDLE_IMAGE_NAME_FORMAT, NUMBER_OF_IDLE_IMAGES);
    }

    /**
     * Executes the jump action for the Avatar, updating its state and notifying observers.
     */
    private void jump() {
        isGrounded = false;

        // notify to all observers that the avatar has jumped.
        for (Runnable observer : jumpObservers) {
            observer.run();
        }
    }

    /**
     * Selects the appropriate animation for the Avatar based on its current state and movement direction.
     *
     * @param state    The current state of the Avatar.
     * @param movement The current movement vector of the Avatar.
     */
    private void chooseAnimation(AvatarState state, Vector2 movement) {
        if (state == AvatarState.IDLE) {
            this.renderer().setRenderable(idleAnimation);
        } else if ((state == AvatarState.JUMP || state == AvatarState.FALLING) &&
                movement.approximatelyEquals(Vector2.ZERO)) {
            this.renderer().setRenderable(jumpAnimation);
        } else if (movement.x() >= NOT_MOVING_IN_X) {
            this.renderer().setRenderable(runAnimation);
            this.renderer().setIsFlippedHorizontally(false);
        } else {
            this.renderer().setRenderable(runAnimation);
            this.renderer().setIsFlippedHorizontally(true);
        }
    }

    /**
     * Determines the energy penalty based on the Avatar's current state.
     *
     * @param state The current state of the Avatar.
     * @return The energy penalty as a float.
     */
    private static float getPenalty(AvatarState state) {
        float penalty = 0;

        switch (state) {
            case IDLE:
                penalty = IDLE_PENALTY;
                break;
            case JUMP:
                penalty = JUMP_PENALTY;
                break;
            case WALKING:
                penalty = WALK_PENALTY;
                break;
            case FALLING:
                penalty = FALL_PENALTY;
                break;
        }
        return penalty;
    }

    /**
     * Determines the current state of the Avatar based on movement and grounding.
     *
     * @param horizontal The horizontal movement vector.
     * @param vertical   The vertical movement vector.
     * @return The Avatar's current state.
     */
    private AvatarState determineState(Vector2 horizontal, Vector2 vertical) {
        if (!vertical.approximatelyEquals(Vector2.ZERO)) {
            return AvatarState.JUMP;
        }

        if (!horizontal.approximatelyEquals(Vector2.ZERO)) {
            return AvatarState.WALKING;
        }

        if (getVelocity().y() == Vector2.ZERO.y()) {
            isGrounded = true;
        } else if (getVelocity().y() / Vector2.DOWN.y() > 0) {
            /**
             * Checks that the current motion vector is in the same direction as DOWN.
             * This means that the avatar can "rest" on a tree stamp and then fall
             * down to the ground later on.
             */
            isGrounded = false;
        }

        if (isGrounded) {
            return AvatarState.IDLE;
        } else {
            return AvatarState.FALLING;
        }
    }
}
