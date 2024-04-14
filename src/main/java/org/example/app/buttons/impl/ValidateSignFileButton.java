package org.example.app.buttons.impl;

import org.example.app.buttons.api.ValidateSignFile;
import org.example.controller.impl.AppController;
import org.example.misc.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

/**
 * Klasa opisująca działanie przycisku do sprawdzania poprawności podpisu i pliku podpisywanego.
 */
public class ValidateSignFileButton extends JButton implements ValidateSignFile, Logger, ActionListener {

    /**
     * Konstruktor przycisku do sprawdzania poprawności.
     *
     * @param listener Kolekcja komponentów nasłuchujących akcje.
     */
    public ValidateSignFileButton(PropertyChangeListener... listener) {
        super("Sprawdź poprawność podpisu");
        this.addActionListener(this);
        for (PropertyChangeListener pcl : listener) {
            this.addPropertyChangeListener("log", pcl);
            this.addPropertyChangeListener("clear", pcl);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.firePropertyChange("clear", false, true);
        String signedFilePath = selectSignedFilePath();
        boolean validate = validateSign(signedFilePath);
        printLog(validate ? "Plik i podpis są poprawne" : "Plik lub podpis zostały naruszone");
    }

    @Override
    public String selectSignedFilePath() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Wybierz plik z podpisem");
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            printLog("Wybrano plik z podpisem");
            return fc.getSelectedFile().getAbsolutePath();
        } else {
            printLog("Nie wybrano pliku");
            return null;
        }
    }

    @Override
    public boolean validateSign(String signedFilePath) {
        return AppController.validateFile(signedFilePath);
    }

    @Override
    public void printLog(String text) {
        this.firePropertyChange("log", null, text);
    }
}
