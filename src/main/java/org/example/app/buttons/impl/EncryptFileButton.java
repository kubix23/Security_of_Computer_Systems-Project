package org.example.app.buttons.impl;

import org.example.app.buttons.api.EncryptFile;
import org.example.app.textFields.api.PasswordInput;
import org.example.controller.impl.AppController;
import org.example.misc.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

/**
 * Klasa opisująca działanie przycisku do szyfrowania pliku
 */
public class EncryptFileButton extends JButton implements EncryptFile, Logger, ActionListener {

    private final PasswordInput passwordComponent;

    /**
     * Konstruktor przycisku do szyfrowania pliku.
     *
     * @param passwordComponent Komponent przechowujący informacje o haśle.
     * @param listener          Kolekcja komponentów nasłuchujących akcje.
     */
    public EncryptFileButton(PasswordInput passwordComponent, PropertyChangeListener... listener) {
        super("Szyfruj plik");
        this.passwordComponent = passwordComponent;
        this.addActionListener(this);
        for (PropertyChangeListener pcl : listener) {
            this.addPropertyChangeListener("log", pcl);
            this.addPropertyChangeListener("clear", pcl);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.firePropertyChange("clear", false, true);
        String privateKeyPath = selectPrivateKeyPath();
        String password = collectPassword();
        String filePath = selectFilePath();
        encryptFile(privateKeyPath, password, filePath);
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
    public String selectPrivateKeyPath() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Wybierz klucz prywatny");
        fc.setFileFilter(new FileNameExtensionFilter(".key", "key"));
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            printLog("Wybrano klucz prywatny");
            return fc.getSelectedFile().getAbsolutePath();
        } else {
            printLog("Nie wybrano klucza prywatnego");
            return null;
        }
    }

    @Override
    public String collectPassword() {
        return this.passwordComponent.getPassword();
    }

    @Override
    public void encryptFile(String privateKeyPath, String password, String filePath) {
        AppController.encrypteFile(privateKeyPath, password, filePath);
        printLog("Zaszyfrowano plik w lokalizaji:");
        printLog(filePath);
    }

    @Override
    public void printLog(String text) {
        this.firePropertyChange("log", null, text);
    }
}
