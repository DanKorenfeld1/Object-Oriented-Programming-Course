package ascii_art;

import ascii_output.AsciiOutput;
import factories.AsciiOutputFactory;
import exceptions.ModelException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Represents the command-line interface for the ASCII art application. This class provides a command loop
 * for the user to interact with the application, setting up the image file, resolution, character set, and
 * output destination, and generating ASCII art from the image.
 */
public class Shell {
    /**
     * The default view for outputting ASCII art, set to console output by default.
     */
    private static final String DEFAULT_OUTPUT_VIEW = AsciiOutputFactory.CONSOLE;

    /**
     * The prompt string displayed to the user in the command-line interface.
     */
    private static final String PROMPT = ">>> ";

    /**
     * Placeholder text used within messages to denote the current resolution.
     */
    private static final String RESOLUTION_PLACEHOLDER = "<current resolution>";

    /**
     * Message format for indicating successful resolution setting.
     */
    private static final String SET_RESOLUTION = "Resolution set to " + RESOLUTION_PLACEHOLDER + ".";

    /**
     * Message displayed when the user enters an invalid command.
     */
    private static final String INVALID_COMMAND = "Did not execute due to incorrect command.";

    /**
     * The command keyword for showing the current character set.
     */
    private static final String SHOW_CHARS_COMMAND = "chars";

    /**
     * The command keyword for adding characters to the character set.
     */
    private static final String ADD_CHARS_COMMAND = "add";

    /**
     * The command keyword for removing characters from the character set.
     */
    private static final String REMOVE_CHARS_COMMAND = "remove";

    /**
     * The command keyword for setting the resolution of the ASCII art.
     */
    private static final String SET_RESOLUTION_COMMAND = "res";

    /**
     * The command keyword for setting the image file to be converted into ASCII art.
     */
    private static final String SET_IMAGE_COMMAND = "image";

    /**
     * The command keyword for specifying the output type (e.g., console, HTML).
     */
    private static final String SET_OUTPUT_COMMAND = "output";

    /**
     * The command keyword for generating and displaying ASCII art from the currently set image.
     */
    private static final String ASCII_ART_COMMAND = "asciiArt";

    /**
     * The command keyword for exiting the application.
     */
    private static final String EXIT_COMMAND = "exit";

    /**
     * A single space string used for splitting input commands and arguments.
     */
    private static final String SPACE = " ";

    /**
     * An empty string used as a default or placeholder in various contexts.
     */
    private static final String EMPTY = "";

    /**
     * The model responsible for the logic and data behind ASCII art generation.
     */
    private final Model model;

    /**
     * The current output destination for ASCII art, which can be console output, HTML file, etc.
     */
    private AsciiOutput view;

    /**
     * A flag indicating whether the main command loop is running.
     */
    private boolean running;

    /**
     * A map associating command strings with their corresponding action handlers.
     */
    private HashMap<String, Consumer> operations;

    /**
     * Initializes a new Shell instance with a specified model for ASCII art generation.
     * Sets up the default output view and initializes command operations.
     *
     * @param model The ASCII art model to use for art generation and management.
     */
    public Shell(Model model) {
        this.model = model;
        AsciiOutputFactory factory = new AsciiOutputFactory();
        this.view = factory.build(DEFAULT_OUTPUT_VIEW);
        this.operations = new HashMap<String, Consumer>();
        set_operations();
    }

    /**
     * Starts the shell, continually prompting the user for commands until the exit command is issued.
     * It parses and executes commands entered by the user, handling ASCII art generation and manipulation.
     */
    public void run() {
        running = true;
        while (running) {
            System.out.print(PROMPT);
            String line = KeyboardInput.readLine();
            try {
                String[] parseLine = parseCommand(line);
                String command = parseLine[0];
                String args = parseLine[1];
                if (operations.containsKey(command)) {
                    operations.get(command).apply(args);
                } else {
                    System.out.println(INVALID_COMMAND);
                }
            } catch (ModelException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Entry point of the application. Initializes the model and shell then starts the shell.
     *
     * @param args Command-line arguments, not used.
     */
    public static void main(String[] args) {
        try {
            AsciiArtModel model = new AsciiArtModel();
            Shell controller = new Shell(model);
            controller.run();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Initializes the operations map with command strings mapping to their corresponding actions.
     */
    private void set_operations() {
        this.operations.putAll(Map.ofEntries(
                Map.entry(SHOW_CHARS_COMMAND, (Consumer) this::chars),
                Map.entry(ADD_CHARS_COMMAND, (Consumer) this::add),
                Map.entry(REMOVE_CHARS_COMMAND, (Consumer) this::remove),
                Map.entry(SET_RESOLUTION_COMMAND, (Consumer) this::res),
                Map.entry(SET_IMAGE_COMMAND, (Consumer) this::image),
                Map.entry(SET_OUTPUT_COMMAND, (Consumer) this::output),
                Map.entry(ASCII_ART_COMMAND, (Consumer) this::asciiArt),
                Map.entry(EXIT_COMMAND, (Consumer) this::exit)
        ));
    }

    /**
     * Handles the "exit" command, terminating the command loop.
     *
     * @param args Command arguments, not used for this command.
     */
    private void exit(String args) {
        running = false;
    }

    /**
     * Generates ASCII art from the currently set image and outputs it using the configured output method.
     *
     * @param args Not used for this command.
     * @throws ModelException If there is an error in ASCII art generation or output.
     */
    private void asciiArt(String args) throws ModelException {
        char[][] output = model.execute();
        view.out(output);
    }

    /**
     * Changes the output destination for ASCII art, such as switching between console and HTML file output.
     *
     * @param args The argument specifying the new output destination.
     * @throws ModelException If the specified output destination is invalid.
     */
    private void output(String args) throws ModelException {
        this.view = model.setOutput(args);
    }

    /**
     * Sets the image file to be used for ASCII art generation.
     *
     * @param args The path to the image file.
     * @throws ModelException If the specified file cannot be found or read.
     */
    private void image(String args) throws ModelException {
        model.setImageFile(args);
    }

    /**
     * Sets the resolution for ASCII art generation.
     *
     * @param args The resolution value as a string.
     * @throws ModelException If the specified resolution is invalid.
     */
    private void res(String args) throws ModelException {
        int current = model.setResolution(args);
        System.out.println(SET_RESOLUTION.replaceAll(RESOLUTION_PLACEHOLDER, Integer.toString(current)));
    }

    /**
     * Removes characters from the set used for ASCII art generation.
     *
     * @param args The characters to remove, specified as a string.
     * @throws ModelException If there is an error removing the characters.
     */
    private void remove(String args) throws ModelException {
        model.remove(args);
    }

    /**
     * Adds characters to the set used for ASCII art generation.
     *
     * @param args The characters to add, specified as a string.
     * @throws ModelException If there is an error adding the characters.
     */
    private void add(String args) throws ModelException {
        model.add(args);
    }

    /**
     * Displays the current set of characters used for ASCII art generation.
     *
     * @param args Not used for this command.
     * @throws ModelException If there is an error retrieving the character set.
     */
    private void chars(String args) throws ModelException {
        char[] chars = model.getCharacterSet();
        for (char c : chars) {
            System.out.print(String.valueOf(c) + SPACE);
        }
        System.out.println(EMPTY);
    }

    /**
     * Parses a command line into a command and arguments.
     *
     * @param line The input line from the user.
     * @return An array where the first element is the command and the second element is the arguments string.
     */
    private static String[] parseCommand(String line) {
        String[] parse = line.split(SPACE, 2);
        String command = parse[0];
        String args = EMPTY;
        if (parse.length > 1) {
            args = parse[1];
        }
        return new String[]{command, args};
    }
}
