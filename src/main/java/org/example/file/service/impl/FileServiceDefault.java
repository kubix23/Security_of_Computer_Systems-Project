package org.example.file.service.impl;

import org.example.file.data.impl.FileLoaderDefault;
import org.example.file.service.api.FileService;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Klasa serwisu odpowiedzialna za przekazanie plików.
 */
public class FileServiceDefault implements FileService {
    @Override
    public byte[] load(String path) {
        try {
            return new FileLoaderDefault().loadFile(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(String path, byte[] data) {
        new FileLoaderDefault().saveFile(path, data);
    }

    @Override
    public BasicFileAttributes getFileAttribute(String path) {
        try {
            return new FileLoaderDefault().getFileAttribute(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(String path) {
        try {
            return new FileLoaderDefault().deleteFile(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
