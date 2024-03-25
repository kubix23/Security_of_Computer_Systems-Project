package org.example;

import javax.swing.*;
import java.awt.*;

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

        JButton jb2 = new JButton("Odszyfruj klucz RSA");
        jb2.addActionListener(e -> new PrivateKeyDecryptor("password"));
        this.add(jb2);

        JButton jb3 = new JButton("Podpisz");
        jb3.addActionListener(e -> {
            try {
                new XAdESsign("password");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        this.add(jb3);

        JButton jb4 = new JButton("Weryfikuj");
        jb4.addActionListener(e -> {
            try {
                new XadESValidator();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        this.add(jb4);

        setVisible(true);
    }
}
