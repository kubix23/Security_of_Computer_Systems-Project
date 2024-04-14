package org.example.app.buttons.impl;

import org.example.app.buttons.api.RSAGenerate;
import org.example.app.textFields.api.PasswordInput;
import org.example.controller.impl.AppController;
import org.example.misc.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

/**
 * Klasa opisująca działanie przycisku do generowania pary kluczy RSA
 */
public class RSAGenerateButton extends JButton implements RSAGenerate, Logger, ActionListener {
    private final PasswordInput passwordComponent;

    /**
     * Konstruktor przycisku do generowania kluczy.
     *
     * @param passwordComponent Komponent przechowujący informacje o haśle.
     * @param listener          Kolekcja komponentów nasłuchujących akcje.
     */
    public RSAGenerateButton(PasswordInput passwordComponent, PropertyChangeListener... listener) {
        super("Generuj klucze RSA");
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
        String path = selectPath();
        String password = collectPassword();
        generateRSA(path, password);
    }

    @Override
    public String selectPath() {
        JFileChooser fc = new JFileChooser("Wybierz miejsce zapisu");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        fc.setAcceptAllFileFilterUsed(false);
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }

    @Override
    public String collectPassword() {
        return this.passwordComponent.getPassword();
    }

    @Override
    public void generateRSA(String path, String password) {
        AppController.setRSAKeys(path, password);
        printLog("Wygenerowano parę kluczy w lokalizacji:");
        printLog(path);
    }

    @Override
    public void printLog(String text) {
        this.firePropertyChange("log", null, text);
    }
}
