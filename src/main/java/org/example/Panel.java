package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Panel extends JFrame{
    public Panel() throws HeadlessException {
        super("Hello World");
        Container container = getContentPane();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setLocation(50,50);
        setLayout(new FlowLayout());
        JButton jb = new JButton("Generuj klucze RSA");
        jb.addActionListener(new RSAgenerator());
        this.add(jb);
        setVisible(true);
    }
}
