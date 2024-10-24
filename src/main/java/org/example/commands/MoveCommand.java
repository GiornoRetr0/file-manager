package org.example.commands;

import org.example.api.Command;
import org.example.api.FileOperation;
import java.nio.file.Path;

public class MoveCommand implements Command {
    private final FileOperation fileOperation;
    private final Path source;
    private final Path target;

    public MoveCommand(FileOperation fileOperation, Path source, Path target) {
        this.fileOperation = fileOperation;
        this.source = source;
        this.target = target;
    }

    @Override
    public void execute() {
        fileOperation.moveFile(source, target);
    }
}
