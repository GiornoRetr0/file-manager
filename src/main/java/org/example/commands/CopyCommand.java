package org.example.commands;

import org.example.api.Command;
import org.example.api.FileOperation;
import org.example.exceptions.FileOperationException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Command to copy files or directories from source to target location
 */
public class CopyCommand implements Command {
    private final FileOperation fileOperation;
    private final Path source;
    private final Path target;
    private boolean executed = false;

    public CopyCommand(FileOperation fileOperation, Path source, Path target) {
        this.fileOperation = fileOperation;
        this.source = source;
        this.target = target;
    }

    @Override
    public boolean validate() throws IllegalArgumentException {
        if (!Files.exists(source)) {
            throw new IllegalArgumentException("Source file does not exist: " + source);
        }
        if (Files.exists(target)) {
            throw new IllegalArgumentException("Target already exists: " + target);
        }
        return true;
    }

    @Override
    public boolean execute() throws FileOperationException {
        try {
            validate();
            fileOperation.copyFile(source, target);
            executed = true;
            return true;
        } catch (IllegalArgumentException e) {
            throw new FileOperationException("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new FileOperationException("Copy failed: " + e.getMessage());
        }
    }

    @Override
    public boolean undo() {
        if (!executed) {
            return false;
        }
        try {
            fileOperation.deleteFile(target);
            executed = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Copies a file or directory from source to target location";
    }

    @Override
    public List<String> getRequiredParameters() {
        return Arrays.asList("source", "target");
    }
}