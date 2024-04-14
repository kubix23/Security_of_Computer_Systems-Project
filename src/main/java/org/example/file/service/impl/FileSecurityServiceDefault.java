package org.example.file.service.impl;

import org.example.controller.impl.AppController;
import org.example.file.funtions.EncryptingDecryptingFileFunction;
import org.example.file.service.api.FileSecurityService;

import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Klasa wywołująca podstawowe operacje szyfrowania i deszyfrowania pliku.
 */
public class FileSecurityServiceDefault implements FileSecurityService {
    @Override
    public void encrypte(String privateKeyPath, String password, String filePath) {
        byte[] fileIn = AppController.loadFile(filePath);
        Path fileInPath = Path.of(filePath);
        PrivateKey privateKey = AppController.getPrivateKey(privateKeyPath, password);
        try {
            byte[] encryptingFile = new EncryptingDecryptingFileFunction().EncryptingFile(privateKey, fileIn);
            AppController.saveFile(
                    fileInPath.getParent() + "\\c" + fileInPath.getFileName(),
                    encryptingFile
            );
            AppController.deleteFile(filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void decrypte(String publicKeyPath, String filePath) {
        byte[] fileIn = AppController.loadFile(filePath);
        Path fileInPath = Path.of(filePath);
        PublicKey publicKey = AppController.getPublicKey(publicKeyPath);
        try {
            byte[] dencryptingFile = new EncryptingDecryptingFileFunction().DencryptingFile(publicKey, fileIn);
            AppController.saveFile(
                    fileInPath.getParent() + "\\" + fileInPath.getFileName().toString().substring(1),
                    dencryptingFile
            );
            AppController.deleteFile(filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
