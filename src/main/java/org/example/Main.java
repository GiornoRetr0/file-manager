package org.example;

import org.example.ui.FileManagerCLI;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide a command (e.g., copy, move, delete, rename).");
            return;
        }
        FileManagerCLI cli = new FileManagerCLI();
        cli.run(args);
    }
}