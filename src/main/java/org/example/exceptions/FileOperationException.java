package org.example.exceptions;

import java.nio.file.Path;

public class FileOperationException extends RuntimeException {
    public FileOperationException(String message) {
        super(message);
    }

    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    // Static methods for specific exception cases
    public static FileOperationException invalidPath(Path path) {
        return new FileOperationException("Invalid path: " + path);
    }

    public static FileOperationException fileAlreadyExists(Path path) {
        return new FileOperationException("File already exists: " + path);
    }

    public static FileOperationException errorMovingFile(Path source, Path target, Throwable cause) {
        return new FileOperationException("Error moving file from " + source + " to " + target, cause);
    }

    public static FileOperationException errorCopyingFile(Path source, Path target, Throwable cause) {
        return new FileOperationException("Error copying file from " + source + " to " + target, cause);
    }

    public static FileOperationException errorRenamingFile(Path source, String newName, Throwable cause) {
        return new FileOperationException("Error renaming file " + source + " to " + newName, cause);
    }

    public static FileOperationException errorDeletingFile(Path path, Throwable cause) {
        return new FileOperationException("Error deleting file: " + path, cause);
    }
}