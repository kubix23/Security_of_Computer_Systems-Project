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
        jb1.addActionListener(new RSAgenerator());
        this.add(jb1);

        JButton jb2 = new JButton("Odszyfruj klucz RSA");
        jb2.addActionListener(e -> new PrivateKeyDecryptor());
        this.add(jb2);

        setVisible(true);
    }
}
