package org.example;

import org.example.ui.FileManagerCLI;
import org.example.ui.FileNavigator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide a command (e.g., copy, move, delete, rename, navigate).");
            return;
        }

        String command = args[0].toLowerCase();
        if (command.equals("navigate")) {
            try {
                new FileNavigator().start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            FileManagerCLI cli = new FileManagerCLI();
            cli.run(args);
        }
    }
}