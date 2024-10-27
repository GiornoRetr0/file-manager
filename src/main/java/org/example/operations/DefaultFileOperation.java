package org.example.operations;

import org.example.api.FileOperation;
import org.example.exceptions.FileOperationException;

import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultFileOperation implements FileOperation {
    private static final Logger logger = Logger.getLogger(DefaultFileOperation.class.getName());

    @Override
    public void moveFile(Path source, Path target) throws FileOperationException {
        try {
            validatePath(source);
            validatePath(target.getParent());
            if (Files.exists(target)) {
                throw FileOperationException.fileAlreadyExists(target);
            }
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            logger.info(String.format("File moved successfully from %s to %s", source, target));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error moving file", e);
            throw FileOperationException.errorMovingFile(source, target, e);
        }
    }

    @Override
    public void copyFile(Path source, Path target) throws FileOperationException {
        try {
            validatePath(source);
            validatePath(target.getParent());
            if (Files.exists(target)) {
                throw FileOperationException.fileAlreadyExists(target);
            }
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            logger.info(String.format("File copied successfully from %s to %s", source, target));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error copying file", e);
            throw FileOperationException.errorCopyingFile(source, target, e);
        }
    }

    @Override
    public void renameFile(Path source, String newName) throws FileOperationException {
        try {
            validatePath(source);
            Path target = source.resolveSibling(newName);
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            logger.info(String.format("File renamed successfully to %s", newName));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error renaming file", e);
            throw FileOperationException.errorRenamingFile(source, newName, e);
        }
    }

    @Override
    public void deleteFile(Path source) throws FileOperationException {
        try {
            validatePath(source);
            Files.delete(source);
            logger.info(String.format("File deleted successfully: %s", source));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error deleting file", e);
            throw FileOperationException.errorDeletingFile(source, e);
        }
    }

    private void validatePath(Path path) throws FileOperationException {
        if (path == null || !Files.exists(path)) {
            throw FileOperationException.invalidPath(path);
        }
    }
}