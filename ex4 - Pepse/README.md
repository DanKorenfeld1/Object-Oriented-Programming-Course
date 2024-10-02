roeey,dankorenfeld


## General Overview of ```trees``` package
    ### Classes We've added to our solution outside of ```trees``` package
        1.
            ```pepse.util.Pair``` - A general (with generics) container of 2 values that can have 2
                                    different types. Used in our ```Tree``` class for returning game objects
                                    with their layer id for the ```PepseGameManager``` to add into the game.

        2.
            ```pepse.world.AvatarState``` - a package-private enum used internally by ```Avatar``` for
                                            describing the current state of the avatar.

    ### Classes within trees package
        1.
            ```Tree``` - A class which represents an entire tree, i.e. the tree stamp, leaves & fruits.
                         Each ```Tree``` is composed of one stamp, multiple leaves & multiple fruits.
                         Therefor ```Tree``` is composed of one ```GameObject``` representing the tree stamp
                         and aggregates ```GameObject``` representing leaves, and aggregates ```Fruit```
                         objects.
        2.
            ```Fruit``` - A class inheriting from ```GameObject``` so we could override it's
                          ```onCollisionEnter``` method. We've decided to use inheritance in this case since
                          ```GameObject``` didn't provided a callback strategy mechanism like the one
                          already implemented for ```update``` via the ```addComponent```.
                          ```Fruit``` accepts 3 callables to use:
                          1. method reference to ``Avatar::addJumpObserver``` (called
                             ```addJumpTracker```).
                          2. method reference to ```PepseGameManager::removeObject``` (called ```adder```).
                          3. method reference to ```PepseGameManager::addObject``` (called ```remover```).
                          ```Fruit``` registers a callback to be called when the avatar jumps (via
                          ```addJumpTracker```), this callback changes it's color.
                          When the Avatar collides with the fruit it's ```onCollisionEnter``` is invoked
                          and the fruit uses ```remover``` to remove itself from the game and also the fruit
                          creates a new scheduled task for adding itself back to game with the help of
                          ```adder``.

    ### Visible Object (within ```trees``` package) that are just plain ```GameObject```
        1.
            Tree stamp - there were no need to inherit from ```GameObject``` in this case since we don't
                         need the stamp to implement any specific functionality that can't be set with
                         public setters of ```GameObject``` such as
                         ```physics().setMass(GameObjectPhysics.IMMOVABLE_MASS)```.

        2.
            Tree leaves - there were no need to inherit from ```GameObject``` in this case since we don't
                          need the leaves to implement any specific functionality that can't be achieved
                          with other classes such as ```Transition``` and ```ScheduledTask```.

    ### Relationships between Classes
        The ```Flora``` class creates ```Tree``` instances within ```Flora::createInRange``` method.
        The ```Flora``` class uses a method reference to ```Terrain::groundHeightAt``` passed to it's
        constructor (from ```PepseGameManager```) to inform the created ```Tree``` objects about
        their height. So ```Flora``` uses ```Terrain``` without adding it as a direct dependency.
        The ```Tree``` class creates and aggregates ```GameObject`` & ```Fruit``` instances.
        The ```Tree``` class just passes method references (passed to him by ```Flora```) of
        ```PepseGameManager::removeObject``` and ```PepseGameManager::addObject``` to his ```Fruit```
        instances.
        The ```Fruit``` object extends ```GameObject``` and uses method references passed to him from
        ```Tree``` as described earlier. So ```Fruit``` uses ```Avatar``` and ```PepseGameManager``` without
        adding them as direct dependencies.

## Design patterns used throughout our solution
    1.
        ### Observer pattern combined with Command pattern
            The way we've decided to implement the avatar jump notification mechanism was by using the
            Observer design pattern as follows:
            1.
                The ```Avatar``` is the Observer subject which has a public method for registering new
                observers, i.e. ```Avatar::addJumpObserver``` method.
                Each observer is an instance of ```Runnable```.
            2.
                The ```PepseGameManager``` passes a reference to the ```Avatar::addJumpObserver``` method to
                ```Tree``` through ```Flora```.
            3.
                Each ```GameObject``` created by ```Tree``` uses that reference to
                ```Avatar::addJumpObserver``` in order to add their observer (callback) into the avatar's
                observers list. For example, the ```Fruit``` registers a callback which changes it's color.
            4.
                When the avatar jumps (it's state is ```AvatarState::JUMP```) he iterates over
                all of his observers and calls their ```.run()``` method.

            Actually it's the same mechanism implemented by ```GameObject::addComponent``` for adding
            functionality to the ```update``` method.

    2.
        ### Facade pattern
            In our implementation the ```Flora``` class is a Facade which hides the complex creation
            of the forest (```Tree``` objects) with their random placement on the terrain.
            The ```Tree``` class is also a Facade hiding the creation of the stamp, leaves and fruits and
            their randomized placement.
