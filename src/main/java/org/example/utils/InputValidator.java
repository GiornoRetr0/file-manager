package org.example.utils;

import java.nio.file.Files;
import java.nio.file.Path;

public class InputValidator {

    // Validates if the given path is a valid Path
    public static boolean isValidPath(String path){
        try{
            Path p = Path.of(path);
            return Files.exists(p);
        }catch (Exception e){
            return false;
        }
    }

    // Checks if a given path exists
    public static boolean doesPathExist(Path path) {
        return Files.exists(path);
    }

    // Checks if a given path is a directory
    public static boolean isDirectory(Path path) {
        return Files.isDirectory(path);
    }

    // Checks if a given path is a file
    public static boolean isFile(Path path) {
        return Files.isRegularFile(path);
    }

    // Checks if the given path has read and write permissions
    public static boolean hasReadWritePermissions(Path path) {
        return Files.isReadable(path) && Files.isWritable(path);
    }

    // Checks if source and target paths are the same
    public static boolean arePathsDifferent(Path source, Path target) {
        return !source.equals(target);
    }

    // Validates if a string is not empty or null
    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }
}

