package org.example.app.buttons.api;

/**
 * Interfejs opisująca działanie komponentu do szyfrowania pliku
 */
public interface EncryptFile {
    /**
     * Funkcja wywołująca okienko dialogowe {@link javax.swing.JFileChooser JFileChooser} do wyboru lokalizacji pliku do odszyfrowania.
     *
     * @return Ścieżka do pliku, który chcemy odszyfrować.
     */
    String selectFilePath();

    /**
     * Funkcja wywołująca okienko dialogowe {@link javax.swing.JFileChooser JFileChooser} do wyboru lokalizacji klucza prywatnego.
     *
     * @return Ścieżka do klucza prywatnego.
     */
    String selectPrivateKeyPath();

    /**
     * Funkcja pobierające hasło z komponentu {@link org.example.app.textFields.api.PasswordInput PasswordInput}.
     *
     * @return Wpisane hasło.
     */
    String collectPassword();

    /**
     * Funkcja szyfrująca plik przy pomocy algorytmu RSA, na podstawie wybranego klucza prywatnego.
     *
     * @param privateKeyPath Ścieżka do położenia klucza prywatnego.
     * @param password       Hasło do odszyfrowania klucza prywatnego.
     * @param filePath       Ścieżka do pliku, który chcemy odszyfrować.
     */
    void encryptFile(String privateKeyPath, String password, String filePath);
}
