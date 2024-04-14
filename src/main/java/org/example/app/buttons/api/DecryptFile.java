package org.example.app.buttons.api;

/**
 * Interfejs opisująca działanie komponentu do deszyfrowania pliku
 */
public interface DecryptFile {
    /**
     * Funkcja wywołująca okienko dialogowe {@link javax.swing.JFileChooser JFileChooser} do wyboru lokalizacji pliku do zaszyfrowania.
     *
     * @return Ścieżka do pliku, który chcemy zaszyfrować.
     */
    String selectFilePath();

    /**
     * Funkcja wywołująca okienko dialogowe {@link javax.swing.JFileChooser JFileChooser} do wyboru lokalizacji klucza publicznego.
     *
     * @return Ścieżka do klucza publicznego.
     */
    String selectPublicKeyPath();

    /**
     * Funkcja odszyfrująca plik przy pomocy algorytmu RSA, na podstawie wybranego klucza publicznego.
     *
     * @param publicKeyPath Ścieżka do położenia klucza publicznego.
     * @param filePath      Ścieżka do pliku, który chcemy zaszyfrować.
     */
    void decryptFile(String publicKeyPath, String filePath);
}
