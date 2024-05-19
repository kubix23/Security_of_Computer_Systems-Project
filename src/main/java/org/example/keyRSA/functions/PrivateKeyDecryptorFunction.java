package org.example.keyRSA.functions;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Klasa implementująca funkcję deszyfrującą klucz prywatny.
 */
public class PrivateKeyDecryptorFunction {
    private final PrivateKey pr;

    /**
     * Konstruktor odpowiedzialny za odszyfrowanie klucza prywatnego
     *
     * @param password          Hasło do odszyfrowania klucza prywatnego.
     * @param privateKeyAESByte Tablica bajtów klucza prywatnego.
     */
    public PrivateKeyDecryptorFunction(String password, byte[] privateKeyAESByte) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), password.toUpperCase().getBytes(), 65536, 256);
            SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(cipher.doFinal(privateKeyAESByte));
            pr = keyFactory.generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException |
                 NoSuchPaddingException | InvalidKeyException | InvalidKeySpecException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return Zwraca odszyfrowany klucz prywatny.
     */
    public PrivateKey getPr() {
        return pr;
    }
}
