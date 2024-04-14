package org.example.app.buttons.impl;

import org.example.app.buttons.api.ShowRSAKeys;
import org.example.app.textFields.api.PasswordInput;
import org.example.controller.impl.AppController;
import org.example.misc.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Klasa opisująca działanie przycisku do wyświetlenia pary kluczy RSA
 */
public class ShowRSAKeysButton extends JButton implements ShowRSAKeys, Logger, ActionListener {

    private final PasswordInput passwordComponent;

    /**
     * Konstruktor przycisku do wyświetlenia kluczy.
     *
     * @param passwordComponent Komponent przechowujący informacje o haśle.
     * @param listener          Kolekcja komponentów nasłuchujących akcje.
     */
    public ShowRSAKeysButton(PasswordInput passwordComponent, PropertyChangeListener... listener) {
        super("Pokaż klucze RSA");
        this.passwordComponent = passwordComponent;
        this.addActionListener(this);
        for (PropertyChangeListener pcl : listener) {
            this.addPropertyChangeListener("log", pcl);
            this.addPropertyChangeListener("clear", pcl);
        }
    }

    public ShowRSAKeysButton(PasswordInput passwordComponent) {
        super("Pokaż klucz publiczny i prywatny");
        this.passwordComponent = passwordComponent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.firePropertyChange("clear", false, true);
        String privateKeyPath = selectPrivateKeyPath();
        String publicKeyPath = selectPublicKeyPath();
        String password = collectPassword();
        KeyPair keyPair = getKeyPair(privateKeyPath, publicKeyPath, password);
        printLog(keyPair.getPrivate().toString() + '\n' + keyPair.getPublic().toString());
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

    private String fileChooserConfig(JFileChooser fc) {
        fc.setFileFilter(new FileNameExtensionFilter(".key", "key"));
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }

    @Override
    public KeyPair getKeyPair(String privateKeyPath, String publicKeyPath, String password) {
        PrivateKey privK = AppController.getPrivateKey(privateKeyPath, password);
        PublicKey pubK = AppController.getPublicKey(publicKeyPath);
        return new KeyPair(pubK, privK);
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

