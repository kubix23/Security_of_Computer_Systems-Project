package org.example.app.buttons.impl;

import org.example.app.buttons.api.DecryptFile;
import org.example.controller.impl.AppController;
import org.example.misc.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

/**
 * Klasa opisująca działanie przycisku do deszyfrowania pliku
 */
public class DecryptFileButton extends JButton implements DecryptFile, Logger, ActionListener {

    /**
     * Konstruktor przycisku do deszyfrowania pliku.
     *
     * @param listener Kolekcja komponentów nasłuchujących akcje.
     */
    public DecryptFileButton(PropertyChangeListener... listener) {
        super("Odszyfruj plik");
        this.addActionListener(this);
        for (PropertyChangeListener pcl : listener) {
            this.addPropertyChangeListener("log", pcl);
            this.addPropertyChangeListener("clear", pcl);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.firePropertyChange("clear", false, true);
        String publicKeyPath = selectPublicKeyPath();
        String filePath = selectFilePath();
        decryptFile(publicKeyPath, filePath);
    }

    @Override
    public String selectFilePath() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Wybierz plik do zaszyfrowania");
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            printLog("Wybrano plik do zaszyfrowania");
            return fc.getSelectedFile().getAbsolutePath();
        } else {
            printLog("Nie wybrano pliku");
            return null;
        }
    }

    @Override
    public String selectPublicKeyPath() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Wybierz klucz publiczny");
        fc.setFileFilter(new FileNameExtensionFilter(".key", "key"));
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            printLog("Wybrano klucz publiczny");
            return fc.getSelectedFile().getAbsolutePath();
        } else {
            printLog("Nie wybrano klucza publicznego");
            return null;
        }
    }

    @Override
    public void decryptFile(String publicKeyPath, String filePath) {
        AppController.decrypteFile(publicKeyPath, filePath);
        printLog("Odszyfrowano plik w lokalizaji:");
        printLog(filePath);
    }

    @Override
    public void printLog(String text) {
        this.firePropertyChange("log", null, text);
    }
}
