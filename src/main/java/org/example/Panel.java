package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.concurrent.ThreadLocalRandom;

public class Panel extends JFrame{
    public Panel() throws HeadlessException {
        super("Hello World");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 700);
        setLocation(50,50);
        setLayout(new GridLayout(0, 1));

        JTextArea jta1 = new JTextArea("Tutaj pokazuje się status programu");
        jta1.setWrapStyleWord(true);
        jta1.setLineWrap(true);
        jta1.setEditable(false);
        jta1.setFocusable(false);
        JScrollPane js1 = new JScrollPane(jta1);
        js1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        js1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(js1);

        JLabel jl1 = new JLabel("Tutaj wpisz hasło:");
        jl1.setVerticalAlignment(SwingConstants.BOTTOM);
        JTextArea jta2 = new JTextArea("Hasło wpisane tutaj będzie wykorzystywane przez wszystke funkcje programu");
        jl1.setLabelFor(jta2);
        this.add(jl1);
        this.add(jta2);

        JButton jb1 = new JButton("Generuj klucze RSA");
        jb1.addActionListener(e -> {
            RSAgenerator g = new RSAgenerator(jta2.getText());
            jta1.setText("Wygenerowano klucze RSA w lokalizacji " + g.getPath());
        });
        this.add(jb1);

        JButton jb2 = new JButton("Odszyfruj klucz prywatny RSA");
        jb2.addActionListener(e -> {
            try {
                jta1.setText(new PrivateKeyDecryptor(jta2.getText()).getPr().toString());
            } catch (Exception ex) {
                jta1.setText("Nie udało się odczytać klucza prywatnego");
                throw new RuntimeException(ex);
            }
        });
        this.add(jb2);

        JButton jb5 = new JButton("Pokaż klucz publiczny RSA");
        jb5.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Wybierz klucz publiczny");
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                try (FileInputStream fos = new FileInputStream(fc.getSelectedFile().getAbsolutePath())) {
                    ObjectInputStream o = new ObjectInputStream(fos);
                    jta1.setText(o.readObject().toString());
                } catch (IOException | ClassNotFoundException ex) {
                    jta1.setText("Nie udało się odczytać klucza publicznego");
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
                    PrivateKey prk = new PrivateKeyDecryptor(jta2.getText()).getPr();

                    byte[] challenge = new byte[10000];
                    ThreadLocalRandom.current().nextBytes(challenge);

                    Signature sig = Signature.getInstance("SHA256withRSA");
                    sig.initSign(prk);
                    sig.update(challenge);
                    byte[] signature = sig.sign();

                    sig.initVerify(puk);
                    sig.update(challenge);

                    boolean keyPairMatches = sig.verify(signature);
                    jta1.setText("Czy klucze są parą: " + (keyPairMatches ? "Tak" : "Nie"));

                } catch (Exception ex) {
                    jta1.setText("Nie udało się odczytać pary kluczy");
                    throw new RuntimeException(ex);
                }
            }
        });
        this.add(jb6);

        JButton jb3 = new JButton("Podpisz");
        jb3.addActionListener(e -> {
            try {
                XAdESsign xades = new XAdESsign(jta2.getText());
                jta1.setText("Podpisano plik w lokalizacji: " + xades.getFilePath());
            } catch (Exception ex) {
                jta1.setText("Nie udało się podpisać pliku");
                System.out.println(ex.getMessage());
            }
        });
        this.add(jb3);

        JButton jb4 = new JButton("Weryfikuj");
        jb4.addActionListener(e -> {
            try {
                XadESValidator xadesv = new XadESValidator();
                jta1.setText(xadesv.getValidate() ? "Plik i podpis są poprawne" : "Plik lub podpis zostały naruszone");
            } catch (Exception ex) {
                jta1.setText("Nie udało się sprawdzić poprawności plików");
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
                    EncryptingDecryptingFile.EncryptingFile(jta2.getText(), fileIn, fileOut);
                    jta1.setText("Wybrany plik został zaszyfrowany: " + fileOut.getAbsolutePath());
                } catch (Exception ex) {
                    jta1.setText("Nie udało się zaszyfrować pliku");
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
                    EncryptingDecryptingFile.DencryptingFile(jta2.getText(), fileIn, fileOut);
                    jta1.setText("Wybrany plik został odszyfrowany: " + fileOut.getAbsolutePath());
                } catch (Exception ex) {
                    jta1.setText("Nie udało się odszyfrować pliku");
                    throw new RuntimeException(ex);
                }
            }
        });
        this.add(jb8);

        setVisible(true);
    }
}
