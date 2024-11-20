package org.example.commands;

import org.example.api.Command;
import org.example.api.FileOperation;
import org.example.exceptions.FileOperationException;
import java.nio.file.*;
import java.util.*;

public class MoveCommand implements Command {
    private final FileOperation fileOperation;
    private final Path source;
    private final Path target;
    private boolean executed = false;

    public MoveCommand(FileOperation fileOperation, Path source, Path target) {
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
        if (!Files.isWritable(source.getParent()) || !Files.isWritable(target.getParent())) {
            throw new IllegalArgumentException("Insufficient permissions for move operation");
        }
        return true;
    }

    @Override
    public boolean execute() throws FileOperationException {
        try {
            validate();
            fileOperation.moveFile(source, target);
            executed = true;
            return true;
        } catch (IllegalArgumentException e) {
            throw new FileOperationException("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new FileOperationException("Move failed: " + e.getMessage());
        }
    }

    @Override
    public boolean isUndoable() {
        return executed && Files.exists(target);
    }

    @Override
    public boolean undo() {
        if (!isUndoable()) {
            return false;
        }
        try {
            fileOperation.moveFile(target, source);
            executed = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getDescription() {
        return "Moves a file or directory from source to target location";
    }

    @Override
    public List<String> getRequiredParameters() {
        return Arrays.asList("source", "target");
    }
}
