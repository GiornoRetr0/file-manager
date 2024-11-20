package org.example.commands;

import org.example.api.Command;
import org.example.api.FileOperation;
import org.example.exceptions.FileOperationException;
import java.nio.file.*;
import java.util.*;

public class DeleteCommand implements Command {
    private final FileOperation fileOperation;
    private final Path source;
    private boolean executed = false;

    public DeleteCommand(FileOperation fileOperation, Path source) {
        this.fileOperation = fileOperation;
        this.source = source;
    }

    @Override
    public boolean validate() throws IllegalArgumentException {
        if (!Files.exists(source)) {
            throw new IllegalArgumentException("File does not exist: " + source);
        }
        if (!Files.isWritable(source)) {
            throw new IllegalArgumentException("File is not writable: " + source);
        }
        return true;
    }

    @Override
    public boolean execute() throws FileOperationException {
        try {
            validate();
            fileOperation.deleteFile(source);
            executed = true;
            return true;
        } catch (IllegalArgumentException e) {
            throw new FileOperationException("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new FileOperationException("Delete failed: " + e.getMessage());
        }
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Deletes a file or directory";
    }

    @Override
    public List<String> getRequiredParameters() {
        return Collections.singletonList("source");
    }
}
