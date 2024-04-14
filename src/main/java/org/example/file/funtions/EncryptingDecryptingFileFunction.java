package org.example.file.funtions;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Klasa implementująca podstawowe operacje szyfrowania i deszyfrowania pliku.
 */
public class EncryptingDecryptingFileFunction {
    /**
     * Szyfruje wybrane dane.
     *
     * @param privateKey Klucz prywatny, którym szyfrujemy dane.
     * @param fileIn     Zawartość, jaką chcemy zaszyfrować.
     * @return Zaszyfrowana zawartość.
     */
    public byte[] EncryptingFile(PrivateKey privateKey, byte[] fileIn)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(fileIn);
    }

    /**
     * Odszyfrujemy wybrany plik.
     *
     * @param publicKey Klucz prywatny, którym odszyfrujemy dane.
     * @param fileIn    Zawartość, jaką chcemy odszyfrować.
     * @return Odszyfrowana zawartość.
     */
    public byte[] DencryptingFile(PublicKey publicKey, byte[] fileIn) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(fileIn);
    }
}

