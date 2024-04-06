package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.*;
import java.util.concurrent.ThreadLocalRandom;

public class Panel extends JFrame{
    public Panel() throws HeadlessException {
        super("Hello World");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setLocation(50,50);
        setLayout(new FlowLayout());

        JButton jb1 = new JButton("Generuj klucze RSA");
        jb1.addActionListener(e -> new RSAgenerator("password"));
        this.add(jb1);

        JButton jb2 = new JButton("Odszyfruj klucz prywatny RSA");
        jb2.addActionListener(e -> System.out.println(new PrivateKeyDecryptor("password").getPr()));
        this.add(jb2);

        JButton jb5 = new JButton("Pokaż klucz publiczny RSA");
        jb5.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Wybierz klucz publiczny");
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                try (FileInputStream fos = new FileInputStream(fc.getSelectedFile().getAbsolutePath())) {
                    ObjectInputStream o = new ObjectInputStream(fos);
                    System.out.println(o.readObject());
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        this.add(jb5);

        JButton jb6 = new JButton("Sprawdź poprawność kluczy RSA");
        jb6.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Wybierz klucz publiczny");
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                try (FileInputStream fos = new FileInputStream(fc.getSelectedFile().getAbsolutePath())) {
                    ObjectInputStream o = new ObjectInputStream(fos);
                    PublicKey puk = (PublicKey) o.readObject();
                    PrivateKey prk = new PrivateKeyDecryptor("password").getPr();

                    byte[] challenge = new byte[10000];
                    ThreadLocalRandom.current().nextBytes(challenge);

                    Signature sig = Signature.getInstance("SHA256withRSA");
                    sig.initSign(prk);
                    sig.update(challenge);
                    byte[] signature = sig.sign();

                    sig.initVerify(puk);
                    sig.update(challenge);

                    boolean keyPairMatches = sig.verify(signature);
                    System.out.println(keyPairMatches);

                } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | SignatureException |
                         InvalidKeyException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        this.add(jb6);

        JButton jb3 = new JButton("Podpisz");
        jb3.addActionListener(e -> {
            try {
                new XAdESsign("password");
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });
        this.add(jb3);

        JButton jb4 = new JButton("Weryfikuj");
        jb4.addActionListener(e -> {
            try {
                new XadESValidator();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });
        this.add(jb4);

        JButton jb7 = new JButton("Szyfruj plik");
        jb7.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Wybierz plik");
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File fileIn = fc.getSelectedFile();
                File fileOut = new File(fc.getSelectedFile().toPath().getParent() + "\\c" + fc.getSelectedFile().toPath().getFileName());
                try {
                    EncryptingDecryptingFile.EncryptingFile("password", fileIn, fileOut);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        this.add(jb7);

        JButton jb8 = new JButton("Odszyfruj plik");
        jb8.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Wybierz plik");
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File fileIn = fc.getSelectedFile();
                File fileOut = new File(fc.getSelectedFile().toPath().getParent() + "\\" + fc.getSelectedFile().getName().substring(1));
                try {
                    EncryptingDecryptingFile.DencryptingFile("password", fileIn, fileOut);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        this.add(jb8);

        setVisible(true);
    }
}
