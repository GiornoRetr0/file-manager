package org.example.commands;

import org.example.api.Command;
import org.example.api.FileOperation;
import org.example.exceptions.FileOperationException;

import java.nio.file.Path;

public class CompressCommand implements Command {
    private final FileOperation fileOperation;
    private final Path source;

    public CompressCommand(FileOperation fileOperation, Path source) {
        this.fileOperation = fileOperation;
        this.source = source;
    }

    @Override
    public boolean execute() throws FileOperationException {
        try {
            fileOperation.compressFile(source);
            return true;
        } catch (Exception e) {
            throw FileOperationException.errorCompressingFile(source);
        }
    }
}
