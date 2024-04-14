package org.example;


import org.example.app.mainPanel.impl.Panel;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {
        System.setProperty("com.sun.org.apache.xml.internal.security.ignoreLineBreaks", "true");
        new Panel();
    }
}