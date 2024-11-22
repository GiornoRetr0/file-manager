package org.example.commands;

import org.example.api.Command;
import org.example.api.FileOperation;
import org.example.exceptions.FileOperationException;
import java.nio.file.*;
import java.util.*;

public class RenameCommand implements Command {
    private final FileOperation fileOperation;
    private final Path source;
    private final String newName;
    private String oldName;
    private boolean executed = false;

    public RenameCommand(FileOperation fileOperation, Path source, String newName) {
        this.fileOperation = fileOperation;
        this.source = source;
        this.newName = newName;
    }

    @Override
    public boolean validate() throws IllegalArgumentException {
        if (!Files.exists(source)) {
            throw new IllegalArgumentException("Source file does not exist: " + source);
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("New name cannot be empty");
        }
        Path parent = source.getParent();
        Path newPath = parent.resolve(newName);
        if (Files.exists(newPath)) {
            throw new IllegalArgumentException("File with new name already exists: " + newName);
        }
        if (!Files.isWritable(parent)) {
            throw new IllegalArgumentException("Insufficient permissions to rename file");
        }
        return true;
    }

    @Override
    public boolean execute() throws FileOperationException {
        try {
            validate();
            oldName = source.getFileName().toString();
            fileOperation.renameFile(source, newName);
            executed = true;
            return true;
        } catch (IllegalArgumentException e) {
            throw new FileOperationException("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new FileOperationException("Rename failed: " + e.getMessage());
        }
    }

    @Override
    public boolean isUndoable() {
        return executed && oldName != null;
    }

    @Override
    public boolean undo() {
        if (!isUndoable()) {
            return false;
        }
        try {
            Path parent = source.getParent();
            Path renamedFile = parent.resolve(newName);
            fileOperation.renameFile(renamedFile, oldName);
            executed = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

//    @Override
//    public String getDescription() {
//        return "Renames a file or directory";
//    }
//
//    @Override
//    public List<String> getRequiredParameters() {
//        return Arrays.asList("source", "newName");
//    }
}
