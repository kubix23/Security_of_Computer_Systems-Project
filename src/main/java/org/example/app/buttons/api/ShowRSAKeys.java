package org.example.app.buttons.api;

import java.security.KeyPair;

/**
 * Interfejs opisująca działanie komponentu do wyświetlenia pary kluczy RSA
 */
public interface ShowRSAKeys {
    /**
     * Funkcja wywołująca okienko dialogowe {@link javax.swing.JFileChooser JFileChooser} do wyboru lokalizacji klucza prywatnego.
     *
     * @return Ścieżka do klucza prywatnego.
     */
    String selectPrivateKeyPath();

    /**
     * Funkcja wywołująca okienko dialogowe {@link javax.swing.JFileChooser JFileChooser} do wyboru lokalizacji klucza publicznego.
     *
     * @return Ścieżka do klucza publicznego.
     */
    String selectPublicKeyPath();

    /**
     * @param privateKeyPath Ścieżka do położenia klucza prywatnego.
     * @param publicKeyPath  Ścieżka do położenia klucza publicznego.
     * @param password       Hasło do odszyfrowania klucza prywatnego.
     * @return Para kluczy prywatny i publiczny.
     */
    KeyPair getKeyPair(String privateKeyPath, String publicKeyPath, String password);

    /**
     * Funkcja pobierające hasło z komponentu {@link org.example.app.textFields.api.PasswordInput PasswordInput}.
     *
     * @return Wpisane hasło.
     */
    String collectPassword();

}
