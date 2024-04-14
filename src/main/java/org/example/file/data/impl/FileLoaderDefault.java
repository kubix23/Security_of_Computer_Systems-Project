package org.example.file.data.impl;

import org.example.file.data.api.FileLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Klasa odpowiedzialna za dostęp do plików.
 */
public class FileLoaderDefault implements FileLoader {
    @Override
    public void saveFile(String path, byte[] data) {
        try {
            Files.write(Path.of(path), data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BasicFileAttributes getFileAttribute(String path) throws IOException {
        return Files.readAttributes(Path.of(path), BasicFileAttributes.class);
    }

    @Override
    public byte[] loadFile(String path) throws IOException {
        return Files.readAllBytes(Path.of(path));
    }

    @Override
    public boolean deleteFile(String path) throws IOException {
        return Files.deleteIfExists(Path.of(path));
    }
}
