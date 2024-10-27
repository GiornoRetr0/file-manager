package org.example.ui;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileNavigator {
    private Path currentDirectory;

    public FileNavigator() {
        this.currentDirectory = Paths.get(System.getProperty("user.dir"));
    }

    public void start() throws IOException {
        Terminal terminal = TerminalBuilder.terminal();
        LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

        label:
        while (true) {
            terminal.puts(InfoCmp.Capability.clear_screen);
            terminal.flush();
            displayFiles(terminal);

            String line = reader.readLine("Navigate (up/down/exit): ").trim().toLowerCase();
            switch (line) {
                case "exit":
                    break label;
                case "up":
                    navigateUp();
                    break;
                case "down":
                    navigateDown();
                    break;
            }
        }
    }

    private void displayFiles(Terminal terminal) throws IOException {
        List<Path> files = Files.list(currentDirectory).collect(Collectors.toList());
        for (Path file : files) {
            terminal.writer().println(file.getFileName().toString());
        }
        terminal.flush();
    }

    private void navigateUp() {
        currentDirectory = currentDirectory.getParent();
    }

    private void navigateDown() {
        // Implement logic to navigate down into a directory
    }

    public static void main(String[] args) {
        try {
            new FileNavigator().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}