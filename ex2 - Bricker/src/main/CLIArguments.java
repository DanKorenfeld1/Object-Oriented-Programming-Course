package src.bricker.main;

/**
 * Parses and stores command line arguments for configuring the game setup, such as the number of bricks per
 * line
 * and the number of lines of bricks.
 */
public class CLIArguments {
    /**
     * The default number of bricks per line if not specified in the command line arguments.
     */
    private static final int NUMBER_OF_BRICKS_PER_LINE_DEFAULT = 8;

    /**
     * The default number of lines of bricks if not specified in the command line arguments.
     */
    private static final int NUMBER_OF_BRICK_LINES_DEFAULT = 7;

    /**
     * The index in the command line arguments array for the number of bricks per line.
     */
    private static final int BRICKS_PER_LINES_INDEX = 0;

    /**
     * The index in the command line arguments array for the number of lines of bricks.
     */
    private static final int LINES_OF_BRICKS_INDEX = 1;

    /**
     * The number of lines of bricks configured through command line arguments or default.
     */
    private int numberOfLines;

    /**
     * The number of bricks per line configured through command line arguments or default.
     */
    private int bricksPerLine;

    /**
     * The array of command line arguments passed to the application.
     */
    private String[] args;

    /**
     * Constructs a CLIArguments instance with the provided command line arguments.
     *
     * @param args Command line arguments passed to the application.
     */
    public CLIArguments(String[] args) {
        this.args = args;
    }


    /**
     * Retrieves the number of bricks per line as specified by the command line arguments or defaults.
     *
     * @return The number of bricks per line.
     */
    public int getBricksPerLine() {
        return bricksPerLine;
    }

    /**
     * Retrieves the number of lines of bricks as specified by the command line arguments or defaults.
     *
     * @return The number of lines of bricks.
     */
    public int getNumberOfLines() {
        return numberOfLines;
    }

    /**
     * Validates the command line arguments and sets the configuration for the number of bricks per line and
     * the number of lines of bricks. Uses default values if specific arguments are not provided.
     *
     * @return true if the arguments are valid and processed successfully, false otherwise.
     */
    public boolean validate() {
        switch (args.length) {
            case 0:
                bricksPerLine = NUMBER_OF_BRICKS_PER_LINE_DEFAULT;
                numberOfLines = NUMBER_OF_BRICK_LINES_DEFAULT;
                break;
            case 1:
                bricksPerLine = Integer.parseInt(args[BRICKS_PER_LINES_INDEX]);
                numberOfLines = NUMBER_OF_BRICK_LINES_DEFAULT;
                break;
            case 2:
                bricksPerLine = Integer.parseInt(args[BRICKS_PER_LINES_INDEX]);
                numberOfLines = Integer.parseInt(args[LINES_OF_BRICKS_INDEX]);
                break;
            default:
                return false;
        }
        return true;
    }
}
