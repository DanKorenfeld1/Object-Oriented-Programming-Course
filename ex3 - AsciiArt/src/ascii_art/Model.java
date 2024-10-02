package ascii_art;

import ascii_output.AsciiOutput;
import exceptions.ModelException;

/**
 * Represents the model for the ASCII art application. This interface defines the operations that can be
 * performed to modify the state of the application, such as adding or removing characters from the
 * character set, setting the resolution, image file, and output destination, and executing the ASCII art
 * generation process.
 */
public interface Model {

    /**
     * Adds characters to the character set used for ASCII art generation.
     *
     * @param args The characters to be added, could be a single character or a range of characters.
     * @throws ModelException If there's an error adding the characters.
     */
    void add(String args) throws ModelException; //char c (or range of chars)

    /**
     * Removes characters from the character set used for ASCII art generation.
     *
     * @param args The characters to be removed, could be a single character or a range of characters.
     * @throws ModelException If there's an error removing the characters.
     */
    void remove(String args) throws ModelException; //char c (or range of chars)

    /**
     * Sets the character set used for ASCII art generation.
     *
     * @param chars The array of characters to be set as the new character set.
     */
    void setCharacterSet(char[] chars);

    /**
     * Retrieves the current character set used for ASCII art generation.
     *
     * @return An array of characters in the current set.
     * @throws ModelException If there's an error retrieving the character set.
     */
    char[] getCharacterSet() throws ModelException; //check if the command like "chars x", if we need to
    // throw exception due to the "x" (added chars)

    /**
     * Sets the resolution for ASCII art generation.
     *
     * @param args The resolution value as a string.
     * @return The resolution value that was set.
     * @throws ModelException If the specified resolution is invalid or there's an error setting it.
     */
    int setResolution(String args) throws ModelException; //int resolution
    //    int setResolution(int resolution) throws ModelException;

    /**
     * Sets the image file to be used for ASCII art generation.
     *
     * @param args The path to the image file.
     * @throws ModelException If the file cannot be found or read.
     */
    void setImageFile(String args) throws ModelException; //String file

    /**
     * Sets the output destination for ASCII art.
     *
     * @param args The output destination type as a string (e.g., "console", "html").
     * @return The AsciiOutput instance corresponding to the set output destination.
     * @throws ModelException If there's an error setting the output destination.
     */
    AsciiOutput setOutput(String args) throws ModelException; //String output

    /**
     * Executes the ASCII art generation process using the current settings (image, character set,
     * resolution).
     *
     * @return A 2D char array representing the generated ASCII art.
     * @throws ModelException If there's an error during the ASCII art generation process.
     */
    char[][] execute() throws ModelException;
}
