package org.example;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class EncryptingDecryptingFile {

    private static Cipher getCipher(String password, int mode) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidKeyException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), password.toUpperCase().getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(mode, secret);
        return cipher;
    }

    static void EncryptingFile(String password, File fileIn, File fileOut) throws Exception {

        Cipher cipher = getCipher(password, Cipher.ENCRYPT_MODE);

        try (FileOutputStream file = new FileOutputStream(fileOut);
             CipherOutputStream cipherOut = new CipherOutputStream(file, cipher);
             FileInputStream content = new FileInputStream(fileIn)) {
            cipherOut.write(content.readAllBytes());
        }
        if (fileIn.delete()) System.out.println("Usunięto plik");
    }

    static void DencryptingFile(String password, File fileIn, File fileOut) throws Exception {

        Cipher cipher = getCipher(password, Cipher.DECRYPT_MODE);

        try (FileInputStream file = new FileInputStream(fileIn);
             CipherInputStream cipherIn = new CipherInputStream(file, cipher);
             FileOutputStream content = new FileOutputStream(fileOut)) {
            content.write(cipherIn.readAllBytes());
        }
        if (fileIn.delete()) System.out.println("Usunięto plik");
    }
}

