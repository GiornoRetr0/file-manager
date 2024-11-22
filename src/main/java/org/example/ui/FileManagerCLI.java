package org.example.ui;

import org.example.api.Command;
import org.example.commands.*;
import org.example.exceptions.FileOperationException;
import org.example.operations.DefaultFileOperation;
import java.nio.file.*;
import java.util.*;
import java.util.Stack;

public class FileManagerCLI {
    private final DefaultFileOperation fileOperation;
    private final Path currentDirectory;
    private final Map<String, CommandFactory> commandMap;
    private final Stack<Command> commandHistory;
    private final Map<String, String> commandHelp;

    public FileManagerCLI() {
        this.fileOperation = new DefaultFileOperation();
        this.currentDirectory = Paths.get(System.getProperty("user.dir"));
        this.commandMap = new HashMap<>();
        this.commandHistory = new Stack<>();
        this.commandHelp = new HashMap<>();
        initializeCommands();
        initializeHelp();
    }

    private void initializeHelp() {
        commandHelp.put("move", "move <source> <target> - Move a file/directory to target location");
        commandHelp.put("copy", "copy <source> <target> - Copy a file/directory to target location");
        commandHelp.put("rename", "rename <file> <newName> - Rename a file/directory");
        commandHelp.put("delete", "delete <file> - Delete a file/directory");
        commandHelp.put("compress", "compress <source> - Compress file into zip archive");
        commandHelp.put("navigate", "navigate - Open interactive file navigator");
        commandHelp.put("undo", "undo - Undo last operation if possible");
    }

    private void initializeCommands() {
        commandMap.put("move", (args) -> new MoveCommand(fileOperation, resolvePath(args[1]), resolvePath(args[2])));
        commandMap.put("copy", (args) -> new CopyCommand(fileOperation, resolvePath(args[1]), resolvePath(args[2])));
        commandMap.put("rename", (args) -> new RenameCommand(fileOperation, resolvePath(args[1]), args[2]));
        commandMap.put("delete", (args) -> new DeleteCommand(fileOperation, resolvePath(args[1])));
        commandMap.put("compress", (args) -> new CompressCommand(fileOperation, resolvePath(args[1])));
        commandMap.put("navigate", (args) -> new FileNavigator());
    }

    public void run(String[] args) {
        if (args.length == 0) {
            displayHelp();
            return;
        }

        String command = args[0].toLowerCase();
        if ("help".equals(command)) {
            displayHelp();
            return;
        }

        if ("undo".equals(command)) {
            handleUndo();
            return;
        }

        CommandFactory commandFactory = commandMap.get(command);
        if (commandFactory == null) {
            System.out.println("Invalid command. Use 'help' to see available commands.");
            return;
        }

        try {
            validateArguments(command, args);
            Command cmd = commandFactory.createCommand(args);
            if (cmd.execute()) {
                if (cmd.isUndoable()) {
                    commandHistory.push(cmd);
                }
                System.out.println("Operation completed successfully.");
            }
        } catch (FileOperationException e) {
            System.out.println("Operation failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid arguments: " + e.getMessage());
            System.out.println("Usage: " + commandHelp.get(command));
        } catch (Exception e) {  // Add generic exception handler
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    private void handleUndo() {
        if (commandHistory.isEmpty()) {
            System.out.println("No operations to undo.");
            return;
        }

        Command lastCommand = commandHistory.peek();
        if (lastCommand.undo()) {
            commandHistory.pop();
            System.out.println("Operation undone successfully.");
        } else {
            System.out.println("Failed to undo last operation.");
        }
    }

    private void  validateArguments(String command, String[] args) {
        int requiredArgs = switch (command) {
            case "move", "copy" -> 3;
            case "rename" -> 3;
            case "delete" -> 2;
            case "compress" -> 2;
            case "navigate" -> 1;
            default -> 0;
        };

        if (args.length < requiredArgs) {
            throw new IllegalArgumentException("Insufficient arguments");
        }
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        commandHelp.values().forEach(help -> System.out.println("  " + help));
    }

    private Path resolvePath(String pathStr) {
        Path path = Paths.get(pathStr);
        return path.isAbsolute() ? path : currentDirectory.resolve(path);
    }

    @FunctionalInterface
    private interface CommandFactory {
        Command createCommand(String[] args);
    }
}