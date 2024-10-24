package org.example.api;

import org.example.exceptions.FileOperationException;

import java.nio.file.Path;

public interface FileOperation {
    void moveFile(Path source, Path target) throws FileOperationException;
    void copyFile(Path source, Path target) throws FileOperationException;
    void renameFile(Path source, String newName) throws FileOperationException;
    void deleteFile(Path source) throws FileOperationException;
}
