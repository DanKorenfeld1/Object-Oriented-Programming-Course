package src.bricker.gameobjects;

import danogl.GameObject;
import src.bricker.main.BrickerGameManager;

/**
 * Represents a callable action that can be executed in the game context.
 */
public interface Callable {

    /**
     * Executes an action involving a game manager and a tracked game object.
     *
     * @param manager The game manager responsible for game state and logic.
     * @param trackedObject The game object being tracked or influenced by this call.
     */
    void call(BrickerGameManager manager, GameObject trackedObject);
}
