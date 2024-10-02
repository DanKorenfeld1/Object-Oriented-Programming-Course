package ascii_art;

import exceptions.ModelException;

/**
 * Functional interface for performing operations that accept a single string argument and may throw a
 * ModelException.
 * This interface is designed to facilitate actions within the ASCII art application, such as command
 * processing,
 * where operations may require a string input to modify the state of the model or perform specific tasks, and
 * where these operations might encounter issues that result in a ModelException being thrown.
 */
@FunctionalInterface
public interface Consumer {
    /**
     * Applies this operation to the given argument.
     *
     * @param args The input argument for the operation.
     * @throws ModelException If an error occurs during the operation execution, related to the model's
     *                        state or processing.
     */
    void apply(String args) throws ModelException;
}
