package org.example;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class RSAgenerator {

    private final String password;
    private String path;

    public RSAgenerator(String password) {
        this.password = password;
        FolderChooser fc = new FolderChooser("Wybierz miejsce zapisu");
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
            path = fc.getSelectedFile().getAbsolutePath();
            createRSAFile(fc.getSelectedFile().getAbsolutePath());
        }
    }

    public String getPath() {
        return path;
    }

    private void createRSAFile(String rootPath){
        try {
            KeyPair pair = createRSA();
            SealedObject privateKey = keyAESEncryption(pair.getPrivate());

            saveKey( rootPath + "/private.key", privateKey);
            saveKey(rootPath + "/public.key", pair.getPublic());
        } catch (RuntimeException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private KeyPair createRSA(){
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(4096);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private SealedObject keyAESEncryption(PrivateKey privateKey) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), password.toUpperCase().getBytes(), 65536, 256);
            SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            return new SealedObject(privateKey, cipher);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeySpecException |
                 IOException | InvalidKeyException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void saveKey(String savePath, Object key){
        Boolean exception = Boolean.FALSE;
        try (FileOutputStream fos = new FileOutputStream(savePath)) {
            ObjectOutputStream o = new ObjectOutputStream(fos);
            o.writeObject(key);
        } catch(Exception e){
            exception = Boolean.TRUE;
            throw new RuntimeException("Key didn't save");
        } finally {
            if (!exception)
                System.out.println("Key saved");
            // System.out.println(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        }
    }
}
