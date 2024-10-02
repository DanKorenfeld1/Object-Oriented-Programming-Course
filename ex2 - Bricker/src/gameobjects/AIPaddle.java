package src.bricker.gameobjects;

import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents an AI-controlled paddle in the game. This paddle automatically moves
 * horizontally to follow a specified game object, typically the ball.
 */
public class AIPaddle extends GameObject {

	private static final float AI_PADDLE_SPEED = 400;
	private static final float THRESHOLD = 10;
	private GameObject objectToFollow;
	private Vector2 dimensions;
	private Vector2 windowDimensions;

	/**
	 * Construct a new GameObject instance.
	 *
	 * @param topLeftCorner Position of the object, in window coordinates (pixels).
	 *                      Note that (0,0) is the top-left corner of the window.
	 * @param dimensions    Width and height in window coordinates.
	 * @param renderable    The renderable representing the object. Can be null, in which case
	 *                      the GameObject will not be rendered.
	 */
	public AIPaddle(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
					GameObject objectToFollow, Vector2 windowDimensions) {
		super(topLeftCorner, dimensions, renderable);
		this.objectToFollow = objectToFollow;
		this.dimensions = dimensions;
		this.windowDimensions = windowDimensions;
	}

	/**
	 * Updates the AI paddle's position based on the object it is following.
	 * Ensures the paddle stays within the game window's boundaries.
	 *
	 * @param deltaTime The time elapsed, in seconds, since the last frame. This is used
	 *                  to update the paddle's position based on its velocity.
	 */
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		Vector2 movement = Vector2.ZERO;

		if (getCenter().x() - objectToFollow.getCenter().x() > THRESHOLD) {
			// object is on the left of us.
			movement = Vector2.LEFT;
		}
		if (objectToFollow.getCenter().x() - getCenter().x() > THRESHOLD) {
			// object is on the right of us.
			movement = Vector2.RIGHT;
		}

		Vector2 topLeftCorner = getTopLeftCorner();
		if (topLeftCorner.x() < 0) {
			// out-of-bounds (left)
			setTopLeftCorner(new Vector2(0f, topLeftCorner.y()));
		} else if(topLeftCorner.x() + dimensions.x() > windowDimensions.x()) {
			// out-of-bounds (right)
			setTopLeftCorner(new Vector2(windowDimensions.x() - dimensions.x(), topLeftCorner.y()));
		}

		setVelocity(movement.mult(AI_PADDLE_SPEED));
	}
}
