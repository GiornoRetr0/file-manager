// FileManagerCLI.java
package org.example.ui;

import org.example.api.Command;
import org.example.commands.*;
import org.example.exceptions.FileOperationException;
import org.example.operations.DefaultFileOperation;
import org.example.utils.InputValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileManagerCLI {
    private final DefaultFileOperation fileOperation;
    private final Path currentDirectory;
    private final Map<String, CommandFactory> commandMap;

    public FileManagerCLI() {
        this.fileOperation = new DefaultFileOperation();
        this.currentDirectory = Paths.get(System.getProperty("user.dir"));
        this.commandMap = new HashMap<>();
        initializeCommands();
    }

    private void initializeCommands() {
        commandMap.put("move", (args) -> new MoveCommand(fileOperation, resolvePath(args[1]), resolvePath(args[2])));
        commandMap.put("copy", (args) -> new CopyCommand(fileOperation, resolvePath(args[1]), resolvePath(args[2])));
        commandMap.put("rename", (args) -> new RenameCommand(fileOperation, resolvePath(args[1]), args[2]));
        commandMap.put("delete", (args) -> new DeleteCommand(fileOperation, resolvePath(args[1])));
        commandMap.put("navigate",  (args) -> new FileNavigator());
    }

    public void run(String[] args) {
        if (args.length == 0) {
            System.out.println("No command provided. Available commands: move, copy, rename, delete");
            return;
        }

        String command = args[0].toLowerCase();
        CommandFactory commandFactory = commandMap.get(command);

        if (commandFactory == null) {
            System.out.println("Invalid command. Available commands: move, copy, rename, delete");
            return;
        }

        try {
            Command cmd = commandFactory.createCommand(args);
            cmd.execute();
        } catch (FileOperationException e) {
            System.out.println("Operation failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid arguments: " + e.getMessage());
        }
    }

    private Path resolvePath(String pathStr) {
        return currentDirectory.resolve(pathStr);
    }

    @FunctionalInterface
    private interface CommandFactory {
        Command createCommand(String[] args);
    }
}