package org.example.app.mainPanel.api;

/**
 * Interfejs opisująca działanie głównego panelu aplikacji.
 */
public interface MainPanel {
    /**
     * Funkcja, w której ustawiane są opcje głównego panelu.
     */
    void config();

    /**
     * Funkcja, w której dodawane są komponenty do głównego panelu.
     */
    void addComponents();
}
