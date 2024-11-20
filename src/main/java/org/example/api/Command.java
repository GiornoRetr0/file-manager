package org.example.api;

import java.util.List;

/**
 * Represents an executable file system command with validation and undo capabilities.
 */
public interface Command {
    /**
     * Validates if the command can be executed with current parameters
     * @return true if command can be executed, false otherwise
     * @throws IllegalArgumentException if command parameters are invalid
     */
    default boolean validate() throws IllegalArgumentException {
        return true;
    }

    /**
     * Executes the command
     * @return true if execution was successful, false otherwise
     * @throws Exception if execution fails
     */
    boolean execute() throws Exception;

    /**
     * Reverses the command execution if possible
     * @return true if undo was successful, false if undo is not supported or failed
     */
    default boolean undo() {
        return false;
    }

    /**
     * @return true if the command can be undone
     */
    default boolean isUndoable() {
        return false;
    }

    /**
     * @return command usage description
     */
    String getDescription();

    /**
     * @return list of required parameters for command
     */
    List<String> getRequiredParameters();
}