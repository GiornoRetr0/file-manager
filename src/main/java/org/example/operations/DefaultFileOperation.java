package org.example.operations;

import org.example.api.FileOperation;
import org.example.api.FileMetadata;
import org.example.exceptions.FileOperationException;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultFileOperation implements FileOperation {
    private static final Logger logger = Logger.getLogger(DefaultFileOperation.class.getName());

    @Override
    public boolean validateOperation(Path source, Path target) throws FileOperationException {
        try {
            validatePath(source);
            if (target != null) {
                validatePath(target.getParent());
                if (Files.exists(target)) {
                    throw new FileOperationException("Target already exists: " + target);
                }
            }
            return true;
        } catch (Exception e) {
            throw new FileOperationException("Validation failed: " + e.getMessage());
        }
    }

    @Override
    public void moveFile(Path source, Path target) throws FileOperationException {
        try {
            validateOperation(source, target);
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
            logger.info(String.format("File moved successfully from %s to %s", source, target));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error moving file", e);
            throw FileOperationException.errorMovingFile(source, target, e);
        }
    }

    @Override
    public void copyFile(Path source, Path target) throws FileOperationException {
        try {
            validateOperation(source, target);
            Files.copy(source, target, StandardCopyOption.COPY_ATTRIBUTES);
            logger.info(String.format("File copied successfully from %s to %s", source, target));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error copying file", e);
            throw FileOperationException.errorCopyingFile(source, target, e);
        }
    }

    @Override
    public void renameFile(Path source, String newName) throws FileOperationException {
        try {
            Path target = source.resolveSibling(newName);
            validateOperation(source, target);
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
            logger.info(String.format("File renamed from %s to %s", source, target));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error renaming file", e);
            throw new FileOperationException("Failed to rename file: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(Path source) throws FileOperationException {
        try {
            validateOperation(source, null);
            Files.delete(source);
            logger.info(String.format("File deleted successfully: %s", source));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error deleting file", e);
            throw new FileOperationException("Failed to delete file: " + e.getMessage());
        }
    }

    @Override
    public FileMetadata getFileInfo(Path path) throws FileOperationException {
        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            String permissions = Files.getPosixFilePermissions(path).toString();
            return new FileMetadata(path, attrs, permissions);
        } catch (IOException e) {
            throw new FileOperationException("Failed to read file metadata: " + path, e);
        }
    }

    private void validatePath(Path path) throws FileOperationException {
        if (path == null) {
            throw new FileOperationException("Path cannot be null");
        }
        if (!Files.exists(path)) {
            throw new FileOperationException("Path does not exist: " + path);
        }
    }
}