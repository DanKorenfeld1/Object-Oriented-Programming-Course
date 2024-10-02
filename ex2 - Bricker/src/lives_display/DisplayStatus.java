package src.bricker.lives_display;

/**
 * Defines the possible states for displaying lives or status indicators in the game.
 */
public enum DisplayStatus {
    /**
     * the game object whose DisplayStatus is ADD should be added to the game by the game manager.
     */
    ADD,

    /**
     * the game object whose DisplayStatus is REMOVE should be removed from the game by the game manager.
     */
    REMOVE,

    /**
     * the game object whose DisplayStatus is DISPLAYED should be already displayed in the game.
     */
    DISPLAYED,

    /**
     * the game object whose DisplayStatus is NONE should be displayed in the game.
     */
    NONE
}
