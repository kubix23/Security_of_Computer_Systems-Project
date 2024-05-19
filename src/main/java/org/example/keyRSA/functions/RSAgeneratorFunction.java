package org.example.keyRSA.functions;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * Klasa implementująca funkcję tworzącą klucz publiczny i prywatny.
 */
public class RSAgeneratorFunction {

    private final String password;
    private final byte[] publicKey;
    private final byte[] privateKey;

    /**
     * Konstruktor odpowiedzialny za wygenerowanie klucza publicznego i prywatnego, następnie zs zaszyfrowanie klucza prywatnego.
     *
     * @param password Hasło do zaszyfrowania klucza prywatnego.
     */
    public RSAgeneratorFunction(String password) {
        this.password = password;
        KeyPair pair = createRSA();
        publicKey = pair.getPublic().getEncoded();
        privateKey = keyAESEncryption(pair.getPrivate());
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    /**
     * Funkcja tworząca parę kluczy RSA.
     *
     * @return Para kluczy RSA.
     */
    private KeyPair createRSA() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(4096);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Funkcja szyfrująca klucz prywatny przy pomocy algorytmu AES.
     *
     * @return Tablica bajtów reprezentująca zaszyfrowany klucz prywatny.
     */
    private byte[] keyAESEncryption(PrivateKey privateKey) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), password.toUpperCase().getBytes(), 65536, 256);
            SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            return cipher.doFinal(privateKey.getEncoded());
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 InvalidKeySpecException | InvalidKeyException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
