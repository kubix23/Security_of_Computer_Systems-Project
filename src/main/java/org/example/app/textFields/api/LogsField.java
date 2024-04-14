package org.example.app.textFields.api;

import java.beans.PropertyChangeListener;

/**
 * Interfejs opisująca działanie komponentu wyświetlającego postępy aplikacji
 */
public interface LogsField extends PropertyChangeListener {

    /**
     * Funkcja czyszcząca wyświetlany tekst
     */
    void clear();

    /**
     * Funkcja wyświetla podany napis.
     */
    void print(String text);
}
