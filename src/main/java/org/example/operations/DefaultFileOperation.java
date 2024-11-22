package org.example.operations;

import org.example.api.FileOperation;
import org.example.api.FileMetadata;
import org.example.exceptions.FileOperationException;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DefaultFileOperation implements FileOperation {
    private static final Logger logger = Logger.getLogger(DefaultFileOperation.class.getName());

    @Override
    public boolean validateOperation(Path source, Path target) throws FileOperationException {
        try {
            validatePath(source);
            if (target != null) {
                validatePath(target.getParent());
                if (Files.exists(target)) {
                    throw FileOperationException.fileAlreadyExists(target);
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
            throw FileOperationException.errorRenamingFile(source, newName, e);
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
            throw FileOperationException.errorDeletingFile(source, e);
        }
    }

    @Override
    public FileMetadata getFileInfo(Path path) throws FileOperationException {
        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            String permissions = Files.getPosixFilePermissions(path).toString();
            return new FileMetadata(path, attrs, permissions);
        } catch (IOException e) {
            throw FileOperationException.errorMetadata(path, e);
        }
    }

    @Override
    public void compressFile(Path source) throws FileOperationException {
        try {
            Path target = Paths.get(source.toString() + ".zip");
            
            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(target))) {
                if (Files.isDirectory(source)) {
                    compressDirectory(source, source.getFileName(), zos);
                } else {
                    compressFile(source, source.getFileName(), zos);
                }
            }
            logger.info(String.format("Successfully compressed %s to %s", source, target));
        } catch (IOException e) {
            throw new FileOperationException("Failed to compress: " + source, e);
        }
    }

    private void compressDirectory(Path sourceDir, Path fileName, ZipOutputStream zos) throws IOException {
        Files.walk(sourceDir)
            .filter(path -> !Files.isDirectory(path))
            .forEach(path -> {
                ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                try {
                    zos.putNextEntry(zipEntry);
                    Files.copy(path, zos);
                    zos.closeEntry();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    private void compressFile(Path source, Path fileName, ZipOutputStream zos) throws IOException {
        ZipEntry zipEntry = new ZipEntry(fileName.toString());
        zos.putNextEntry(zipEntry);
        Files.copy(source, zos);
        zos.closeEntry();
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