package org.example.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import org.example.api.Command;
import org.example.api.FileMetadata;
import org.example.api.FileOperation;
import org.example.exceptions.FileOperationException;
import org.example.operations.DefaultFileOperation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.logging.Logger;

public class FileNavigator implements Command {
    private final FileOperation fileOperation;
    private Path currentDirectory;
    private int selectedIndex = 0;
    private int scrollOffset = 0;
    private Screen screen;
    private List<Path> currentFiles;
    private static final Path LAST_DIR_FILE = Paths.get(System.getProperty("user.home"), ".file_navigator_last_dir");
    private static final Logger logger = Logger.getLogger(FileNavigator.class.getName());

    public FileNavigator() {
        this.currentDirectory = Paths.get(System.getProperty("user.dir"));
        this.fileOperation = new DefaultFileOperation(); // Default implementation
    }


    @Override
    public boolean execute() throws Exception {
        initializeScreen();
        try {
            return handleNavigation();
        } finally {
            saveLastDirectory();
            screen.close();
        }
    }

    private void initializeScreen() throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();
    }

    private boolean handleNavigation() throws IOException {
        while (true) {
            updateFileList();
            drawScreen();
            KeyStroke key = screen.readInput();

            if (key.getKeyType() == KeyType.EOF) {
                return false;
            }

            switch (key.getKeyType()) {
                case ArrowUp -> moveUp();
                case ArrowDown -> moveDown();
                case Enter -> handleEnter();
                case Backspace, Delete -> navigateToParent(); // Handle both keys
                case Escape -> {
                    saveLastDirectory();
                    return true;
                }
                case Character -> {
                    if (key.getCharacter() == 'q') {
                        saveLastDirectory();
                        return true;
                    }
                    if (key.getCharacter() == 'i') {
                        showFileInfo(currentFiles.get(selectedIndex));
                    }
                }
            }
        }
    }

    private void updateFileList() throws IOException {
        currentFiles = Files.list(currentDirectory)
                .sorted((p1, p2) -> {
                    boolean d1 = Files.isDirectory(p1);
                    boolean d2 = Files.isDirectory(p2);
                    if (d1 && !d2) return -1;
                    if (!d1 && d2) return 1;
                    return p1.getFileName().toString()
                            .compareToIgnoreCase(p2.getFileName().toString());
                })
                .collect(Collectors.toList());
    }

    private void drawScreen() throws IOException {
        screen.clear();
        TextGraphics tg = screen.newTextGraphics();
        TerminalSize size = screen.getTerminalSize();

        // Draw header
        tg.setForegroundColor(TextColor.ANSI.WHITE);
        tg.putString(0, 0, "Current directory: " + currentDirectory);
        tg.putString(0, 1, "─".repeat(size.getColumns()));

        // Draw files
        int maxDisplayItems = size.getRows() - 4;
        adjustScrollOffset(maxDisplayItems);

        for (int i = 0; i < Math.min(maxDisplayItems, currentFiles.size()); i++) {
            int fileIndex = i + scrollOffset;
            if (fileIndex >= currentFiles.size()) break;

            Path file = currentFiles.get(fileIndex);
            String fileName = file.getFileName().toString();

            if (fileIndex == selectedIndex) {
                tg.setBackgroundColor(TextColor.ANSI.BLUE);
                tg.setForegroundColor(TextColor.ANSI.WHITE);
            } else {
                tg.setBackgroundColor(TextColor.ANSI.DEFAULT);
                tg.setForegroundColor(Files.isDirectory(file) ?
                        TextColor.ANSI.CYAN : TextColor.ANSI.WHITE);
            }

            tg.putString(1, i + 2, fileName);
            tg.setBackgroundColor(TextColor.ANSI.DEFAULT);
        }

        // Draw footer
        tg.setForegroundColor(TextColor.ANSI.WHITE);
        tg.putString(0, size.getRows() - 1,
                "↑↓:Navigate  Enter:Open  Backspace:Parent  q:Quit  i:Info");

        screen.refresh();
    }

    private void adjustScrollOffset(int maxDisplayItems) {
        if (selectedIndex < scrollOffset) {
            scrollOffset = selectedIndex;
        } else if (selectedIndex >= scrollOffset + maxDisplayItems) {
            scrollOffset = selectedIndex - maxDisplayItems + 1;
        }
    }

    private void moveUp() {
        if (selectedIndex > 0) {
            selectedIndex--;
        }
    }

    private void moveDown() {
        if (selectedIndex < currentFiles.size() - 1) {
            selectedIndex++;
        }
    }

    private void handleEnter() throws IOException {
        Path selected = currentFiles.get(selectedIndex);
        if (Files.isDirectory(selected)) {
            currentDirectory = selected;
            selectedIndex = 0;
            scrollOffset = 0;
        }
    }

    private void navigateToParent() throws IOException {
        Path parent = currentDirectory.getParent();
        if (parent != null) {
            currentDirectory = parent;
            selectedIndex = 0;
            scrollOffset = 0;
            updateFileList();
        }
    }

    private void showFileInfo(Path file) {
        try {
            FileMetadata metadata = fileOperation.getFileInfo(file);
            screen.clear();
            TextGraphics tg = screen.newTextGraphics();
            
            int row = 0;
            tg.putString(0, row++, "File Information:");
            tg.putString(0, row++, "Name: " + metadata.getPath().getFileName());
            tg.putString(0, row++, "Size: " + metadata.getSize() + " bytes");
            tg.putString(0, row++, "Type: " + (metadata.isDirectory() ? "Directory" : "File"));
            tg.putString(0, row++, "Created: " + metadata.getCreationTime());
            tg.putString(0, row++, "Modified: " + metadata.getLastModifiedTime());
            tg.putString(0, row++, "Permissions: " + metadata.getPermissions());
            tg.putString(0, row++, "\nPress any key to continue...");
            
            screen.refresh();
            screen.readInput();
        } catch (IOException | FileOperationException e) {
            // Handle error
        }
    }

    private void saveLastDirectory() {
        try {
            Files.writeString(LAST_DIR_FILE, currentDirectory.toString());
        } catch (IOException e) {
            logger.warning("Failed to save last directory: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "Interactive file navigator";
    }

    @Override
    public List<String> getRequiredParameters() {
        return Collections.emptyList(); // Navigator doesn't need parameters
    }
}