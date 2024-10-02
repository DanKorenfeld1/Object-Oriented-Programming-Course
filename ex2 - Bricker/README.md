roeey,dankorenfeld

1.
    In the dilemma of passing BrickerGameManager or gameObjects() to the CollisionStrategy constructor
    We chose to pass BrickerGameManager & use it's public methods since it's a manager object, i.e.
    it's sole purpose is to manage the game objects.
    If We would have passed gameObjects() it would break the encapsulation of BrickerGameManager since
    gameObjects() is an internal member of BrickerGameManager.
    A disadvantage of this method is that BrickerGameManager has a lot more "power", more than what's required
    by other objects to remove/add themselves or other from the game.


2.
    We have created an interface for each live view option, so that we can support any future view
    in the game manager.
    We created a new class for graphical display, and another class for numerical display.
    The interface exposes to the game manager the ability to notify about the current lives counter
    and retrieve the displaying state of all the display's game objects, which works as follows:
    In the numerical lives display there is only one game object (TextRendrable) so it tells the game manager
    every time that it's already displayed.
    On the countrery the graphic lives display holds an array of game objects (hearts) and he tells the
    game manager which heart should be removed/added based on the lives counter.
    Neither of them adds/removes game objects to the game but they do tell the game manager what
    should be done with their game objects.


3.
    All special strategies inherits from the BasicCollisionStrategy since they all share a common behaviour -
    remove the shatterred brick from the game.

    #### Extra Ball:
        We have added a new class implementing the CollisionStrategy interface which
        adds a new Puck instance to the game.
        The Puck class, inherits from Ball, was created in order to distinguish between the main ball &
        all the extras when they go out of bounds of the window (pucks should be removed, ball should be
        recenterred & life counter should be decreased).

    #### Extra Paddle:
        We have added a new class implementing the CollisionStrategy interface which adds a
        new Paddle instance to the game.
        This paddle is an instance of the same class as the main paddle. The removal of this extra
        paddle is done via an instance of a new class - CollosionTracker.
        The CollisonTracker "keep an eye" on the tracked object's collision counter until a threshold
        is reached & then performs an action determined by the creator of the tracker.
        In this particular case the action is to remove the paddle from the game.

    #### Extra Life:
        We have added a new class implementing the CollisionStrategy interface which adds
        a new Heart instance in inplace of the broken brick.
        The heart falls down, and if the main paddle collides with him then the user gains another
        life (whenever it's possible, i.e. the counter hasn't reached it's maximal value).
        If the heart "falls off" the window then it removes itself from the game (via the game manager).
        The Heart class is used in both this strategy and in the graphic display.

    #### Change camera:
        We have added a new class implementing the CollisionStrategy interface which shifts the focus of the
        camera to the main ball. The removal of the focus shift  is done via an instance of CollosionTracker.
        The CollisonTracker "keep an eye" on the tracked object's collision counter until
        a threshold is reached & then performs an action determined by the creator of the tracker.
        In this particular case the action is to shift back the camera focus from the ball back to
        the center of the game window.

4.  #### Multiple collisionStrategy
    We implemented a decorator around the collision strategy interface. We treated instances of this class as
    nodes in a binary tree of collision strategies. When the onCollision method is called then we traverse the
    tree in a DFS-like manner and call every strategy's onCollision until we've traversed the entire tree.
    The creation of this binary tree of collision strategies is done within the
    CollisionStrategyFactory.getMultipleBehaviorCollisionStrategyDecorator recursive method.
    In this method we pass a capabilities counter which is incremented whenever an actual collision strategy
    is created (as a leaf in the tree) and when this counter reaches the threshold then no more strategies are
    created (null is then used as a leaf in the tree). we build the tree from bottom-right of the tree,
    leftwards and after to upwards.



5.
    ### Changes to API from part 1:
    #### Ball:
        - added public Tag Constant, to be used by the tracking object
        - implements Collidable: used to track the ball's collisions through CollisionTracker within some
        strategies

    #### Brick:
        - added public Tag Constant, to be used by the tracking object
        - override update method: to avoid multiple subtractions of the same frame

    #### Paddle:
        - added public Tag Constant, to be used by the tracking object
        - implements Collidable: used to track the ball's collisions through CollisionTracker within some
            strategies
        - onCollisionEnter: to increase the collision counter, obtained from the getCollisionCounter method
        - getCollisionCounter: required by the Collidable interface

    #### Callable:
        interface, command design pattern- represent a action that should be executed by the collisionTracker
        when threshold is reached.

    #### Collidable:
        interface, required by the CollisionTracker to track the number of collisions of
        the objects (ball or paddle)

    #### CollisionTracker:
        Tracks collisions of a specific object and triggers an action when a collision threshold is reached.
        - constructor
        - update: Tracks collisions of a specific object and triggers an action when a collision threshold is

    #### Heart:
        Represents a heart object in the game
        - constructor
        - shouldCollideWith: use to avoid collision with unwanted objects
        - onCollisionEnter: to increase the life counter.
        - setBrickerGameManager: to set the brickerGameManager object
        - update: to remove the object from the game.

    #### Puck:
        - added public Tag Constant, to avoid collision with hearts
        - constructor
        - update: removes it from the game if it goes out of bounds.

    #### CLIArguments:
        handle CLI arguments
        - constructor
        - getBricksPerLine (getter)
        - getNumberOfLines (getter)
        - validate: check if the arguments are valid and assign default when values needed.

    #### BrickerGameManager:
        - Constants: LIVES_DISPLAY_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT, PUCK_RADUIS,
                     MAX_HEARTS, SPACE_BETWEEN_HEARTS
        - setBricksPerLine (setter)
        - setLinesOfBricks (setter)
        - update: check if the game is over
        - removeObject: let other objects remove themselves or others from the game such as the puck
        - addObject: let other objects add themselves or others to the game such as puck in the strategy
        - getWindowDimantsions (getter)
        - addLife: let the heart increase the life counter when hit by main paddle
