package org.example.api;

import org.example.exceptions.FileOperationException;
import java.nio.file.Path;

/**
 * Interface defining core file system operations with validation and monitoring capabilities.
 */
public interface FileOperation {
    /**
     * Validates if a file operation is possible without executing it
     */
    boolean validateOperation(Path source, Path target) throws FileOperationException;

    /**
     * Moves a file or directory from source to target location
     * @param source Path of the file/directory to move
     * @param target Destination path
     * @throws FileOperationException if operation fails
     */
    void moveFile(Path source, Path target) throws FileOperationException;

    /**
     * Copies a file or directory from source to target location
     * @param source Path of the file/directory to copy
     * @param target Destination path
     * @throws FileOperationException if operation fails
     */
    void copyFile(Path source, Path target) throws FileOperationException;

    /**
     * Renames a file or directory
     * @param source Path of the file/directory to rename
     * @param newName New name for the file/directory
     * @throws FileOperationException if operation fails
     */
    void renameFile(Path source, String newName) throws FileOperationException;

    /**
     * Deletes a file or directory
     * @param source Path of the file/directory to delete
     * @throws FileOperationException if operation fails
     */
    void deleteFile(Path source) throws FileOperationException;

    /**
     * Simulates an operation without actually performing it
     * @return true if operation would succeed
     */
    default boolean dryRun(Path source, Path target) {
        try {
            return validateOperation(source, target);
        } catch (FileOperationException e) {
            return false;
        }
    }

    /**
     * Gets file metadata for given path
     * @return FileMetadata containing size, permissions, etc.
     */
    FileMetadata getFileInfo(Path path) throws FileOperationException;
}

