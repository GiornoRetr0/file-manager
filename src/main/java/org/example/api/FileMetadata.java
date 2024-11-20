package org.example.api;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class FileMetadata {
    private final long size;
    private final FileTime creationTime;
    private final FileTime lastModifiedTime;
    private final FileTime lastAccessTime;
    private final boolean isDirectory;
    private final boolean isRegularFile;
    private final String permissions;
    private final Path path;

    public FileMetadata(Path path, BasicFileAttributes attrs, String permissions) {
        this.path = path;
        this.size = attrs.size();
        this.creationTime = attrs.creationTime();
        this.lastModifiedTime = attrs.lastModifiedTime();
        this.lastAccessTime = attrs.lastAccessTime();
        this.isDirectory = attrs.isDirectory();
        this.isRegularFile = attrs.isRegularFile();
        this.permissions = permissions;
    }

    // Getters
    public long getSize() { return size; }
    public FileTime getCreationTime() { return creationTime; }
    public FileTime getLastModifiedTime() { return lastModifiedTime; }
    public boolean isDirectory() { return isDirectory; }
    public String getPermissions() { return permissions; }
    public Path getPath() { return path; }

    @Override
    public String toString() {
        return String.format(
            "%s (%s) - %dB - Created: %s - Modified: %s - Permissions: %s",
            path.getFileName(),
            isDirectory ? "Directory" : "File",
            size,
            creationTime,
            lastModifiedTime,
            permissions
        );
    }
}
