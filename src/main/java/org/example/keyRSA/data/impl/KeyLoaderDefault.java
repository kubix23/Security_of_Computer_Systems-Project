package org.example.keyRSA.data.impl;

import org.example.keyRSA.data.api.KeyLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Klasa odpowiedzialna za dostęp do kluczy.
 */
public class KeyLoaderDefault implements KeyLoader {
    @Override
    public void saveKey(String path, byte[] data) {
        try {
            Files.write(Path.of(path), data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] loadKey(String path) throws IOException {
        return Files.readAllBytes(Path.of(path));
    }
}
