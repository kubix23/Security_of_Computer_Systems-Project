package org.example.keyRSA.functions;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Klasa implementująca funkcję deszyfrującą klucz publiczny.
 */
public class PublicKeyDecryptorFunction {
    private final PublicKey key;

    /**
     * Konstruktor odpowiedzialny za odszyfrowanie klucza prywatnego
     *
     * @param publicKeyPath Tablica bajtów klucza publicznego.
     */
    public PublicKeyDecryptorFunction(byte[] publicKeyPath) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyPath);
            key = keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @return Zwraca odszyfrowany klucz publiczny.
     */
    public PublicKey getKey() {
        return key;
    }
}
