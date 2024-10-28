package org.example.ui;

import org.example.api.Command;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileNavigator implements Command {
    private Path currentDirectory;
    private int selectedIndex = 0;

    public FileNavigator() {
        this.currentDirectory = Paths.get(System.getProperty("user.dir"));
    }

    @Override
    public void execute() {
        try (Terminal terminal = TerminalBuilder.terminal()) {
            LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

            while (true) {
                terminal.puts(org.jline.utils.InfoCmp.Capability.clear_screen);
                terminal.flush();
                displayFiles(terminal);

                String line = reader.readLine().trim().toLowerCase();
                switch (line) {
                    case "\u001b[C": // Right arrow key
                        navigateDown();
                        break;
                    case "\u001b[D": // Left arrow key
                        navigateUp();
                        break;
                    case "\u001b[A": // Up arrow key
                        moveSelectionUp();
                        break;
                    case "\u001b[B": // Down arrow key
                        moveSelectionDown();
                        break;
                    case "q":
                        return;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayFiles(Terminal terminal) throws IOException {
        List<Path> files = Files.list(currentDirectory).collect(Collectors.toList());
        if (files.isEmpty()) {
            terminal.writer().println("Directory is empty.");
        } else {
            for (int i = 0; i < files.size(); i++) {
                Path file = files.get(i);
                AttributedStringBuilder fileNameBuilder = new AttributedStringBuilder();

                if (Files.isDirectory(file)) {
                    fileNameBuilder.append(file.getFileName().toString(),
                            AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE).bold());
                } else {
                    fileNameBuilder.append(file.getFileName().toString(),
                            AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                }

                // Highlight selected file or folder
                if (i == selectedIndex) {
                    fileNameBuilder.style(AttributedStyle.DEFAULT.background(AttributedStyle.GREEN));
                }

                terminal.writer().println(fileNameBuilder.toAttributedString().toAnsi());
            }
        }
        terminal.flush();
    }

    private void navigateUp() {
        Path parent = currentDirectory.getParent();
        if (parent != null) {
            currentDirectory = parent;
            selectedIndex = 0;
        }
    }

    private void navigateDown() {
        try {
            List<Path> files = Files.list(currentDirectory).collect(Collectors.toList());
            if (selectedIndex < files.size() && Files.isDirectory(files.get(selectedIndex))) {
                currentDirectory = files.get(selectedIndex);
                selectedIndex = 0;
            }
        } catch (IOException e) {
            System.out.println("Cannot navigate into this directory.");
        }
    }

    private void moveSelectionUp() {
        if (selectedIndex > 0) {
            selectedIndex--;
        }
    }

    private void moveSelectionDown() {
        try {
            List<Path> files = Files.list(currentDirectory).collect(Collectors.toList());
            if (selectedIndex < files.size() - 1) {
                selectedIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}