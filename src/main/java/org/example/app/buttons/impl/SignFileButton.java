package org.example.app.buttons.impl;

import org.example.app.buttons.api.SignFile;
import org.example.app.textFields.api.PasswordInput;
import org.example.controller.impl.AppController;
import org.example.misc.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

/**
 * Klasa opisująca działanie przycisku do podpisywania plików.
 */
public class SignFileButton extends JButton implements SignFile, Logger, ActionListener {

    private final PasswordInput passwordComponent;

    /**
     * Konstruktor przycisku do podpisywania plików.
     *
     * @param passwordComponent Komponent przechowujący informacje o haśle.
     * @param listener          Kolekcja komponentów nasłuchujących akcje.
     */
    public SignFileButton(PasswordInput passwordComponent, PropertyChangeListener... listener) {
        super("Podpisz plik");
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
        String publicKeyPath = selectPublicKeyPath();
        String password = collectPassword();
        String signedFilePath = selectSignedFilePath();
        signFile(privateKeyPath, publicKeyPath, signedFilePath, password);
    }

    @Override
    public String selectPrivateKeyPath() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Wybierz klucz prywatny");
        String path = fileChooserConfig(fc);
        if (path == null) {
            printLog("Nie wybrano klucza prywatnego");
        } else {
            printLog("Wybrano klucz prywatny");
        }
        return path;
    }

    @Override
    public String selectPublicKeyPath() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Wybierz klucz publiczny");
        String path = fileChooserConfig(fc);
        if (path == null) {
            printLog("Nie znaleziono klucza publicznego");
        } else {
            printLog("Wybrano klucz publiczny");
        }
        return path;
    }

    @Override
    public String selectSignedFilePath() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Wybierz plik do podpisania");
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            printLog("Wybrano plik do podpisania");
            return fc.getSelectedFile().getAbsolutePath();
        } else {
            printLog("Nie wybrano pliku");
            return null;
        }
    }

    private String fileChooserConfig(JFileChooser fc) {
        fc.setFileFilter(new FileNameExtensionFilter(".key", "key"));
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }

    @Override
    public void signFile(String privateKeyPath, String publicKeyPath, String signedFilePath, String password) {
        AppController.signFile(privateKeyPath, publicKeyPath, signedFilePath, password);
        printLog("Podpisano plik w lokalizacji:");
        printLog(signedFilePath + ".xades");
    }

    @Override
    public String collectPassword() {
        return this.passwordComponent.getPassword();
    }

    @Override
    public void printLog(String text) {
        this.firePropertyChange("log", null, text);
    }
}
