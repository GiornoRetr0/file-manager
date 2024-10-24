// FileManagerCLI.java
package org.example.ui;

import org.example.api.Command;
import org.example.commands.*;
import org.example.exceptions.FileOperationException;
import org.example.operations.DefaultFileOperation;
import org.example.utils.InputValidator;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManagerCLI {
    private final DefaultFileOperation fileOperation;
    private final Path currentDirectory;

    public FileManagerCLI() {
        this.fileOperation = new DefaultFileOperation();
        this.currentDirectory = Paths.get(System.getProperty("user.dir"));
    }

    public void run(String[] args) {
        String command = args[0].toLowerCase();

        try {
            switch (command) {
                case "move":
                    if (args.length < 3) {
                        System.out.println("Usage: move <source> <target>");
                        return;
                    }
                    executeMove(args[1], args[2]);
                    break;
                case "copy":
                    if (args.length < 3) {
                        System.out.println("Usage: copy <source> <target>");
                        return;
                    }
                    executeCopy(args[1], args[2]);
                    break;
                case "rename":
                    if (args.length < 3) {
                        System.out.println("Usage: rename <source> <newName>");
                        return;
                    }
                    executeRename(args[1], args[2]);
                    break;
                case "delete":
                    if (args.length < 2) {
                        System.out.println("Usage: delete <source>");
                        return;
                    }
                    executeDelete(args[1]);
                    break;
                default:
                    System.out.println("Invalid command. Available commands: move, copy, rename, delete");
            }
        } catch (FileOperationException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
    }

    private void executeMove(String sourceStr, String targetStr) {
        Path source = currentDirectory.resolve(sourceStr);
        Path target = currentDirectory.resolve(targetStr);
        if (!InputValidator.doesPathExist(source)) {
            System.out.println("Source file does not exist.");
            return;
        }
        Command moveCommand = new MoveCommand(fileOperation, source, target);
        moveCommand.execute();
    }

    private void executeCopy(String sourceStr, String targetStr) {
        Path source = currentDirectory.resolve(sourceStr);
        Path target = currentDirectory.resolve(targetStr);
        if (!InputValidator.doesPathExist(source)) {
            System.out.println("Source file does not exist.");
            return;
        }
        Command copyCommand = new CopyCommand(fileOperation, source, target);
        copyCommand.execute();
    }

    private void executeRename(String sourceStr, String newName) {
        Path source = currentDirectory.resolve(sourceStr);
        if (!InputValidator.doesPathExist(source)) {
            System.out.println("Source file does not exist.");
            return;
        }
        Command renameCommand = new RenameCommand(fileOperation, source, newName);
        renameCommand.execute();
    }

    private void executeDelete(String sourceStr) {
        Path source = currentDirectory.resolve(sourceStr);
        if (!InputValidator.doesPathExist(source)) {
            System.out.println("Source file does not exist.");
            return;
        }
        Command deleteCommand = new DeleteCommand(fileOperation, source);
        deleteCommand.execute();
    }
}
