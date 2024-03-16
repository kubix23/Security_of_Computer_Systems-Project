package org.example;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class PrivateKeyDecryptor {

    String password = "password";

    public PrivateKeyDecryptor() {
        try {
            FolderChooser fc = new FolderChooser();
            String savePath = "";
            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                savePath = fc.getSelectedFile().getAbsolutePath() + "/private.key";
            }

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), password.toUpperCase().getBytes(), 65536, 256);
            SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            PrivateKey pr = (PrivateKey) loadObject(savePath).getObject(cipher);
            System.out.println(pr);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 InvalidKeySpecException | IOException | InvalidKeyException | ClassNotFoundException |
                 BadPaddingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private SealedObject loadObject(String savePath) {
        Boolean exception = Boolean.FALSE;
        try (FileInputStream fos = new FileInputStream(savePath)) {
            ObjectInputStream o = new ObjectInputStream(fos);
            return (SealedObject) o.readObject();
        } catch (ClassCastException e) {
            exception = Boolean.TRUE;
            throw new RuntimeException("Key didn't save");
        } catch (IOException e) {
            exception = Boolean.TRUE;
            throw new RuntimeException(e.getMessage());
        } catch (ClassNotFoundException e) {
            exception = Boolean.TRUE;
            throw new RuntimeException("Object read error");
        } finally {
            if (!exception)
                System.out.println("Key loaded");
        }
    }
}
