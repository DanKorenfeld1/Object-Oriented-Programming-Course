package ascii_art;

import ascii_output.AsciiOutput;
import factories.AsciiOutputFactory;
import exceptions.ModelException;
import image.Image;
import image.ImageManipulator;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;

/**
 * Implements the Model interface to provide functionality for ASCII art generation.
 * This includes managing character sets, image resolution, image files, output methods,
 * and executing the ASCII art generation algorithm.
 */
public class AsciiArtModel implements Model {
    /**
     * The default resolution for ASCII art generation.
     */
    private static final int DEFAULT_RESOLUTION = 128;

    /**
     * The default filename for the image used in ASCII art generation.
     */
    private static final String DEFAULT_FILE_NAME = "cat.jpeg";

    /**
     * The number of valid characters that can be used in ASCII art generation, based on the character range
     * in SubImgCharMatcher.
     */
    private static final int NUMBER_OF_VALID_CHARS = SubImgCharMatcher.LAST_CHAR_RANGE -
            SubImgCharMatcher.FIRST_CHAR_RANGE + 1;

    /**
     * The delimiter used to split input arguments.
     */
    private static final String SPACE_DELIMITER = " ";

    /**
     * The delimiter used to specify a range of characters.
     */
    private static final String DELIMITER = "-";

    /**
     * The character representation of a space.
     */
    private static final char SPACE = ' ';

    /**
     * The string representation of a space character for use in commands.
     */
    private static final String SPACE_STRING = "space";

    /**
     * The keyword used to specify all characters in a range or set.
     */
    private static final String ALL = "all";

    /**
     * The keyword for console output method.
     */
    private static final String CONSOLE = "console";

    /**
     * The keyword for HTML output method.
     */
    private static final String HTML = "html";

    /**
     * The command argument to increase resolution.
     */
    private static final String UP = "up";

    /**
     * The command argument to decrease resolution.
     */
    private static final String DOWN = "down";

    /**
     * The default character set used for ASCII art generation if none is specified.
     */
    private static final char[] DEFAULT_CHARACTER_SET = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * Error message displayed when an incorrect output format is specified.
     */
    private static final String ERROR_MESSAGE_INCORRECT_OUTPUT = "Did not change output method due to " +
            "incorrect format.";

    /**
     * Error message displayed when there is an issue with the specified image file.
     */
    private static final String ERROR_MESSAGE_IMAGE_FILE = "Did not execute due to problem with image file.";

    /**
     * Error message displayed when adding characters fails due to incorrect format.
     */
    private static final String ERROR_MESSAGE_INCORRECT_ADD = "Did not add due to incorrect format.";

    /**
     * Error message displayed when removing characters fails due to incorrect format.
     */
    private static final String ERROR_MESSAGE_INCORRECT_REMOVE = "Did not remove due to incorrect format.";

    /**
     * The coefficient used to increase the resolution.
     */
    private static final int COEFFICIENT_UP = 2;

    /**
     * The coefficient used to decrease the resolution.
     */
    private static final double COEFFICIENT_DOWN = 0.5;

    /**
     * Error message displayed when an attempt to change resolution fails due to incorrect format.
     */
    private static final String ERROR_MESSAGE_INCORRECT_RESOLUTION = "Did not change resolution due to " +
            "incorrect format.";

    /**
     * Error message displayed when an attempt to change resolution fails due to exceeding defined boundaries.
     */
    private static final String ERROR_MESSAGE_INVALID_BOUNDARIES_RESOLUTION = "Did not change resolution " +
            "due to exceeding boundaries.";

    /**
     * Error message displated when trying to invoke the asciiArt algorithm with an empty charset.
     */
    private static final String ERROR_MESSAGE_EMPTY_CHARSET = "Did not execute. Charset is empty.";

    /**
     * The SubImgCharMatcher instance used for matching sub-images to characters.
     */
    private SubImgCharMatcher subImgCharMatcher;

    /**
     * The current resolution for ASCII art generation.
     */
    private int resolution;

    /**
     * The current image being used for ASCII art generation.
     */
    private Image image;

    /**
     * The factory used to create AsciiOutput instances based on the specified output method.
     */
    private AsciiOutputFactory factory;

    /**
     * Constructs an AsciiArtModel with a specified character set for ASCII art generation.
     * Initializes the model with default resolution and image file.
     *
     * @param charset The character set to be used for ASCII art generation.
     * @throws IOException If there is an error loading the default image file.
     */
    public AsciiArtModel(char[] charset) throws IOException {
        this.subImgCharMatcher = new SubImgCharMatcher(charset);
        resolution = DEFAULT_RESOLUTION;
        image = loadImage(DEFAULT_FILE_NAME);
        factory = new AsciiOutputFactory();
    }

    /**
     * Default constructor that initializes the model with a default character set.
     *
     * @throws IOException If there is an error loading the default image file.
     */
    public AsciiArtModel() throws IOException {
        this(DEFAULT_CHARACTER_SET);
    }

    /**
     * Adds specified characters to the character set for ASCII art generation.
     *
     * @param args The characters to add, which could be a single character, a range, or a special keyword
     *             like "all".
     * @throws ModelException If the format of args is incorrect or addition is not possible.
     */
    @Override
    public void add(String args) throws ModelException {
        char[] toAdd = parseArgs(args);
        if (toAdd == null) {
            throw new ModelException(ERROR_MESSAGE_INCORRECT_ADD);
        }
        for (char c : toAdd) {
            subImgCharMatcher.addChar(c);
        }
    }

    /**
     * Removes specified characters from the character set for ASCII art generation.
     *
     * @param args The characters to remove, which could be a single character, a range, or a special keyword.
     * @throws ModelException If the format of args is incorrect or removal is not possible.
     */
    @Override
    public void remove(String args) throws ModelException {
        char[] toRemove = parseArgs(args);
        if (toRemove == null) {
            throw new ModelException(ERROR_MESSAGE_INCORRECT_REMOVE);
        }
        for (char c : toRemove) {
            subImgCharMatcher.removeChar(c);
        }
    }


    /**
     * Sets the character set to be used for ASCII art generation to the specified array of characters.
     *
     * @param chars The new character set.
     */
    @Override
    public void setCharacterSet(char[] chars) {
        // clear the previous character set.
        for (char c : subImgCharMatcher.getCharSet()) {
            subImgCharMatcher.removeChar(c);
        }

        for (char c : chars) {
            subImgCharMatcher.addChar(c);
        }
    }

    /**
     * Retrieves the current character set used for ASCII art generation.
     *
     * @return The current character set.
     * @throws ModelException If retrieving the character set fails.
     */
    @Override
    public char[] getCharacterSet() throws ModelException {
        return subImgCharMatcher.getCharSet();
    }

    /**
     * Sets the resolution for ASCII art generation based on a specified string argument.
     *
     * @param args The argument specifying how to adjust the resolution (e.g., "up", "down").
     * @return The new resolution value.
     * @throws ModelException If the specified argument is invalid or the resolution adjustment is not
     * possible.
     */
    @Override
    public int setResolution(String args) throws ModelException {
        double coefficient = 0;
        switch (args) {
            case UP:
                coefficient = COEFFICIENT_UP;
                break;
            case DOWN:
                coefficient = COEFFICIENT_DOWN;
                break;
            default:
                throw new ModelException(ERROR_MESSAGE_INCORRECT_RESOLUTION);
        }

        // set resolution is pow of 2, therefore resolution divisible by 2.
        if (!ImageManipulator.isValidResolution(image, (int) (resolution * coefficient))) {
            throw new ModelException(ERROR_MESSAGE_INVALID_BOUNDARIES_RESOLUTION);
        }

        resolution *= coefficient;
        return resolution;
    }

    /**
     * Sets the image file to be used for ASCII art generation to the specified path.
     *
     * @param args The path to the image file.
     * @throws ModelException If the file cannot be found or read.
     */
    @Override
    public void setImageFile(String args) throws ModelException {
        try {
            image = loadImage(parseArgsAsPath(args));
        } catch (IOException e) {
            throw new ModelException(ERROR_MESSAGE_IMAGE_FILE);
        }
    }

    /**
     * Sets the output method for ASCII art based on a specified string argument.
     *
     * @param args The argument specifying the output method (e.g., "console", "html").
     * @return The AsciiOutput object corresponding to the specified output method.
     * @throws ModelException If the specified output method is invalid.
     */
    @Override
    public AsciiOutput setOutput(String args) throws ModelException {
        AsciiOutput output = null;
        String outputName = parseOutput(args);
        switch (outputName) {
            case CONSOLE:
                /* fallthrough */
            case HTML:
                output = factory.build(outputName);
                break;
            default:
                throw new ModelException(ERROR_MESSAGE_INCORRECT_OUTPUT);
        }
        return output;
    }

    /**
     * Executes the ASCII art generation process and returns the result as a 2D char array.
     *
     * @return The ASCII art represented as a 2D char array.
     * @throws ModelException If the ASCII art generation process fails.
     */
    @Override
    public char[][] execute() throws ModelException {
        if (subImgCharMatcher.getCharSet().length == 0) {
            throw new ModelException(ERROR_MESSAGE_EMPTY_CHARSET);
        }

        subImgCharMatcher.histogramEqualization();
        AsciiArtAlgorithm art = new AsciiArtAlgorithm(image, resolution, subImgCharMatcher);
        return art.run();
    }

    /**
     * Loads an image from the specified path.
     *
     * @param path The path to the image file.
     * @return The loaded Image object.
     * @throws IOException If the image file cannot be found or read.
     */
    private Image loadImage(String path) throws IOException {
        return ImageManipulator.padImage(new Image(path));
    }

    /**
     * Parses the argument string to determine which characters to add or remove.
     *
     * @param args The argument string specifying characters.
     * @return An array of characters to be added or removed.
     */
    private char[] parseArgs(String args) {
        String argument = args.split(SPACE_DELIMITER)[0];
        if (argument.equals(ALL)) {
            //add all
            char[] fullCharSet = new char[NUMBER_OF_VALID_CHARS];
            for (char c = SubImgCharMatcher.FIRST_CHAR_RANGE; c <= SubImgCharMatcher.LAST_CHAR_RANGE; c++) {
                fullCharSet[c - SubImgCharMatcher.FIRST_CHAR_RANGE] = c;
            }
            return fullCharSet;
        } else if (argument.equals(SPACE_STRING)) {
            //add space
            return new char[]{SPACE};
        } else if (argument.contains(DELIMITER)) {
            // add range of characters
            return parseRange(argument);
        } else if (isValidRangeChar(argument)) {
            // add single character
            return new char[]{argument.charAt(0)};
        } else {
            return null;
        }
    }

    /**
     * Parses a range of characters from a string. Used when adding or removing a range of characters to/from
     * the character set.
     *
     * @param argument The string specifying the range of characters.
     * @return An array of characters within the specified range.
     */
    private static char[] parseRange(String argument) {
        char firstChar = (char) Math.min(argument.split(DELIMITER)[0].charAt(0),
                argument.split(DELIMITER)[1].charAt(0));
        char lastChar = (char) Math.max(argument.split(DELIMITER)[0].charAt(0),
                argument.split(DELIMITER)[1].charAt(0));
        char[] charSet = new char[lastChar - firstChar + 1];

        for (char c = firstChar; c <= lastChar; c++) {
            if (isValidRangeChar(c)) {
                charSet[c - firstChar] = c;
            } else {
                return null;
            }
        }
        return charSet;
    }

    /**
     * Validates if a string argument represents a valid single character within the allowable range for
     * ASCII art generation.
     *
     * @param argument The string to validate.
     * @return true if the string represents a single valid character; false otherwise.
     */
    private static boolean isValidRangeChar(String argument) {
        return argument.length() == 1 && isValidRangeChar(argument.charAt(0));
    }

    /**
     * Validates if a character is within the allowable range for ASCII art generation.
     *
     * @param c The character to validate.
     * @return true if the character is within the valid range; false otherwise.
     */
    private static boolean isValidRangeChar(char c) {
        return (c >= SubImgCharMatcher.FIRST_CHAR_RANGE && c <= SubImgCharMatcher.LAST_CHAR_RANGE);
    }

    /**
     * Parses a file path from the input argument string. This is used when setting the image file for ASCII
     * art generation.
     *
     * @param args The argument string containing the file path.
     * @return The file path as a string.
     */
    private String parseArgsAsPath(String args) {
        // validate that we have only the image file and not other arguments
        return args.split(SPACE_DELIMITER)[0];
    }

    /**
     * Parses the output method from the input argument string. This is used when setting the output method
     * for ASCII art.
     *
     * @param args The argument string specifying the output method.
     * @return The output method as a string.
     */
    private String parseOutput(String args) {
        // validate that we have only the image file and not other arguments
        return args.split(SPACE_DELIMITER)[0];
    }
}
