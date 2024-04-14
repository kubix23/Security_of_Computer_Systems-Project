package org.example.app.textFields.impl;

import org.example.app.textFields.api.LogsField;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Klasa opisująca działanie pola tekstowego wyświetlającego postępy aplikacji
 */
public class LogsFieldComponent extends JTextArea implements LogsField {

    /**
     * Konstruktor pola tekstowego do wyświetlania informacji
     */
    public LogsFieldComponent() {
        super("Tutaj pokazuje się status programu");
        setBackground(new Color(180, 180, 180));
        setWrapStyleWord(true);
        setLineWrap(true);
        setEditable(false);
        setFocusable(false);
    }

    @Override
    public void clear() {
        this.setText("");
    }

    @Override
    public void print(String text) {
        this.setText(this.getText() + text + "\n");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "log":
                this.print(evt.getNewValue().toString());
                break;
            case "clear":
                this.clear();
                break;
        }
    }
}
