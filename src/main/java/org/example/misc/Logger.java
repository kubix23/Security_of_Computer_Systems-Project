package org.example.misc;

/**
 * Interfejs opisująca działanie funkcji rejestrującej wiadomości w komponencie do tego przeznaczonym.
 */
public interface Logger {

    /**
     * Funkcji rejestrującej wiadomości w komponencie.
     *
     * @param text Tekst wysyłanej wiadomości.
     */
    void printLog(String text);

}
