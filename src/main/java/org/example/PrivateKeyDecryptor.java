package org.example;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class PrivateKeyDecryptor {

    private final String password;
    private PrivateKey pr;

    public PrivateKeyDecryptor(String password) throws Exception {
        this.password = password;
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Wybierz klucz prywatny");
        fc.setFileFilter(new FileNameExtensionFilter(".key", "key"));
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            pr = keyAESDecryption(fc.getSelectedFile().getAbsolutePath());
        }
    }

    public PrivateKey getPr() {
        return pr;
    }

    private PrivateKey keyAESDecryption(String savePath) throws Exception {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), password.toUpperCase().getBytes(), 65536, 256);
            SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            return (PrivateKey) loadObject(savePath).getObject(cipher);
        } catch (ClassNotFoundException | NoSuchAlgorithmException | IllegalBlockSizeException | IOException |
                 NoSuchPaddingException | InvalidKeyException | InvalidKeySpecException e) {
            throw new Exception(e.getMessage());
        } catch (BadPaddingException e) {
            throw new Exception("Invalid password");
        }
    }

    private SealedObject loadObject(String savePath) throws Exception {
        Boolean exception = Boolean.FALSE;
        try (FileInputStream fos = new FileInputStream(savePath)) {
            ObjectInputStream o = new ObjectInputStream(fos);
            return (SealedObject) o.readObject();
        } catch (ClassCastException e) {
            exception = Boolean.TRUE;
            throw new Exception("Key didn't save");
        } catch (IOException e) {
            exception = Boolean.TRUE;
            throw new Exception(e.getMessage());
        } catch (ClassNotFoundException e) {
            exception = Boolean.TRUE;
            throw new Exception("Object read error");
        } finally {
            if (!exception)
                System.out.println("Key loaded");
        }
    }
}
