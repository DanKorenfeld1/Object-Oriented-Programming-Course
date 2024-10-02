roeey,dankorenfeld

1.
    ### Overall Design - UML
    We've used the MVC pattern (Model-View-Controller).
    The ```AsciiArt``` interface is the view, The ```Shell``` is the controller & ```AsciiArtModel``` is the
    model.
    Since there is a command for switching the view (change between console to html and vice versa) we've used
    the factory pattern in ```AsciiOutputFactory```.
    The ```AsciiArtModel``` uses the functional interface ```Consumer``` for his command handlers
    (Command pattern).
    The ```ImageManipulator``` is a stateless object which all of his methods are ```static``` used by other
    objects without any concrete instance.

2.
    ### Data Structures

    #### The Character Set
    We've used different data structures for different purposes & optimizations.
    The character set is stored in ```ArrayList<Character>``` so adding, removing & existence check all
    takes O(n) time.

    #### ```getCharByImageBrightness``` Cache
    We've used a ```HashMap<Double, Character>``` for caching the results of ```getCharByImageBrightness```
    so every
    subsequent call to ```getCharByImageBrightness``` for image of the same brightness won't be computed, but
    retrieved
    from the cache in O(1) amortized time (as long as the character set remains the same, in case of charset
    changes
    the cache is cleared).
    The actual computation of this method takes O(n) since it finds a minimum in a list (the character set),
    however
    it is cached so it will take only O(1) in amortized time.

    #### Un-Normalized Brightness of Characters Cache
    We've used 2 ```HashMap<Character, Double>``` for caching the un-normalized brightness of characters,
    one for
    caching every character the program have encountered in it's entire execution and the other only for the
    current
    character set.

    #### Normalized Brightness of Characters Cache
    We've also used ```HashMap<Character, Double>``` for caching the normalized brightnesses of the characters
    in
    the current character set which removes the need for calculating the normalized brightness of characters
    when
    trying to determine which character should be used to represent an image (in case of charset changes
    this cache is cleared).

    #### Minimal & Maximal Brightness Values
    We've also used min-heap & max-heap (```PriorityQueue<Double>```) for quick access (in O(1)) to the
    minimal & maximal brightness values of the current character set, moreover adding & removing characters
    costs
    O(log(n)) which is negligible since the operations of the character set itself takes O(n).

3.
    ### Exceptions
    The model (```AsciiArtModel```) implements the actual commands & throws ```ModelException``` with
    the required error message when a command is deemed as invalid.
    The controller (```Shell```) which parses inputs & operates the model catches any exception that the model
    might throw (only ```ModelException```, as stated in the ```Model``` interface) & prints it's message.
    For example, in the ```image <filename>``` command the model tries to load the image in the given filename
    , if the
    image cannot be loaded the ```Image``` class throws ```IOException```, which is caught by the model, which
     in turn
    throws ```ModelException``` with the proper error message.

4.
    ### Changes to SubImgCharMatcher API
    We've added the following public constants & methods:
    * ```public char[] getCharSet()```
    * ```public void histogramEqualization()```
    * ```public static final int FIRST_CHAR_RANGE = 32```
    * ```public static final int LAST_CHAR_RANGE = 126```

    We've added the ```getCharSet``` method for the implementation of the "chars" command.
    We could have tracked which characters are stored within the character set at any
    given time since all the add & remove operations are done in the same object (AsciiArtModel).
    However this solution seemed a bit cumbersome in comparison with a simple yet effective getter.

    We've added the ```histogramEqualization``` method so the costly computations required for normalizing
    the brightness values could be done only when needed, i.e. just before the execution of the asciiArt
    algorithm.
    This of course, doesn't even considers caching the results...
    The alternative to this approach is to normalize after every change made
    (which affected the minimal/maximal
    brightness values) to the character set from the ```addChar``` & ```removeCharr``` methods.
    In addition calling ```histogramEqualization``` from ```removeCharr``` could lead to an error when the
    user tries to execute the ```remove all`` command or any other ```remove``` command which drops the amount
    of different brightness values down to 1 or 0.

    ```FIRST_CHAR_RANGE``` & ```LAST_CHAR_RANGE``` were made public since they are used also in
    ```AsciiArtModel```
    parsing method and we wanted to minimize the amount of "ground truth" locations regarding the valid range
    of characters.
5.
    ### Changes to the supplied code API
    We didn't changed the the API of the code provided by the course staff.