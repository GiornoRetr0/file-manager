package org.example.commands;

import org.example.api.Command;
import org.example.api.FileOperation;
import java.nio.file.Path;

public class RenameCommand implements Command {
    private final FileOperation fileOperation;
    private final Path source;
    private final String newName;

    public RenameCommand(FileOperation fileOperation, Path source, String newName) {
        this.fileOperation = fileOperation;
        this.source = source;
        this.newName = newName;
    }

    @Override
    public void execute() {
        fileOperation.renameFile(source, newName);
    }
}
