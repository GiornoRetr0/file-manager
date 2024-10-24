package org.example.commands;

import org.example.api.Command;
import org.example.api.FileOperation;
import java.nio.file.Path;

public class DeleteCommand implements Command {
    private final FileOperation fileOperation;
    private final Path source;

    public DeleteCommand(FileOperation fileOperation, Path source) {
        this.fileOperation = fileOperation;
        this.source = source;
    }

    @Override
    public void execute() {
        fileOperation.deleteFile(source);
    }
}
