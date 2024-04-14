package org.example.app.buttons.api;

/**
 * Interfejs opisująca działanie komponentu do podpisywania plików.
 */
public interface SignFile {
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
     * Funkcja wywołująca okienko dialogowe {@link javax.swing.JFileChooser JFileChooser} do wyboru lokalizacji pliku do podpisania.
     *
     * @return Ścieżka do pliku, który chcemy podpisać.
     */
    String selectSignedFilePath();

    /**
     * Funkcja tworząca podpis w formacie XAdES.
     *
     * @param privateKeyPath Ścieżka do położenia klucza prywatnego.
     * @param publicKeyPath  Ścieżka do położenia klucza publicznego.
     * @param signedFilePath Ścieżka do pliku, który chcemy podpisać.
     * @param password       Hasło do odszyfrowania klucza prywatnego.
     */
    void signFile(String privateKeyPath, String publicKeyPath, String signedFilePath, String password);

    /**
     * Funkcja pobierające hasło z komponentu {@link org.example.app.textFields.api.PasswordInput PasswordInput}.
     *
     * @return Wpisane hasło.
     */
    String collectPassword();
}
