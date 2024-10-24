package org.example.operations;

import org.example.api.FileOperation;
import org.example.exceptions.FileOperationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DefaultFileOperation implements FileOperation {

    @Override
    public void moveFile(Path source, Path target) throws FileOperationException {
        try{
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            System.out.format("File moved successfully from %s to %s", source, target);
        }catch (IOException e) {
            throw new FileOperationException("Error moving file: " + e.getMessage(), e);
        }
    }

    @Override
    public void copyFile(Path source, Path target) throws FileOperationException {
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied successfully from " + source + " to " + target);
        } catch (IOException e) {
            throw new FileOperationException("Error copying file: " + e.getMessage(), e);
        }
    }

    @Override
    public void renameFile(Path source, String newName) throws FileOperationException {
        File file = source.toFile();
        File renamedFile = new File(file.getParent(), newName);
    }

    @Override
    public void deleteFile(Path source) throws FileOperationException {

    }
}
