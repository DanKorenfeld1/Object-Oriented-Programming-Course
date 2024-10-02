package factories;


import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;

/**
 * Factory class for creating different types of ASCII output generators.
 * Supports generating ASCII art outputs to various destinations like HTML files or console.
 */
public class AsciiOutputFactory {
    /**
     * Default font name used for ASCII output generation.
     */
    private static final String DEFAULT_FONT = "Courier New";

    /**
     * Default filename for the output when generating HTML ASCII art.
     */
    private static final String DEFAULT_FILE_NAME = "out.html";

    /**
     * Constant representing the HTML output type.
     */
    public static final String HTML = "html";

    /**
     * Constant representing the console output type.
     */
    public static final String CONSOLE = "console";

    /**
     * Font name used for generating ASCII output.
     */
    private final String fontName;

    /**
     * Filename used for saving ASCII output, applicable when generating HTML output.
     */
    private final String filename;

    /**
     * Default constructor initializing the ASCIIOutputFactory with default filename and font.
     */
    public AsciiOutputFactory() {
        this(DEFAULT_FILE_NAME, DEFAULT_FONT);
    }

    /**
     * Constructor for AsciiOutputFactory allowing specification of output filename and font name.
     *
     * @param filename The filename for the output, used when generating HTML ASCII art.
     * @param fontName The font name to be used for ASCII art generation.
     */
    public AsciiOutputFactory(String filename, String fontName) {
        this.fontName = fontName;
        this.filename = filename;
    }

    /**
     * Builds and returns an ASCII output generator based on the specified type.
     * Currently supports HTML and console output types.
     *
     * @param name The type of ASCII output generator to build. Use "html" for HTML output and "console"
     *             for console output.
     * @return An instance of AsciiOutput corresponding to the specified type, or null if the type is not
     * recognized.
     */
    public AsciiOutput build(String name) {
        switch (name) {
            case HTML:
                return new HtmlAsciiOutput(filename, fontName);
            case CONSOLE:
                return new ConsoleAsciiOutput();
            default:
                return null;
        }
    }

}
