package exceptions;

/**
 * Exception class for the ASCII art application.
 * This class is used to indicate that an error occurred within the model of the ASCII art application.
 */
public class ModelException extends Exception {

    /**
     * Constructs a new ModelException with the specified error message.
     *
     * @param errorMessage The detail message.
     */
    public ModelException(String errorMessage) {
        super(errorMessage);
    }
}