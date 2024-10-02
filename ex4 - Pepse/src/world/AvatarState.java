package src.pepse.world;

/**
 * Represents the possible states of an Avatar within the game.
 * Each state corresponds to a different set of animations and behaviors
 * for the Avatar, affecting how it interacts with the game world and responds to user inputs.
 */
public enum AvatarState {
    /**
     * The state where the Avatar is not moving. It triggers the idle animation.
     */
    IDLE,
    /**
     * The state representing the Avatar in the midst of a jump.
     * This state is active from the initiation of the jump until the Avatar begins descending.
     */
    JUMP,
    /**
     * The state where the Avatar is falling towards the ground.
     * This typically occurs after jumping or walking off a platform.
     */
    FALLING,
    /**
     * The state indicating that the Avatar is moving horizontally, either left or right.
     */
    WALKING
}
