package org.example.app.buttons.api;

/**
 * Interfejs opisująca działanie komponentu do generowania pary kluczy RSA
 */
public interface RSAGenerate {
    /**
     * Funkcja wywołująca okienko dialogowe {@link javax.swing.JFileChooser JFileChooser} do wyboru lokalizacji pliku do odszyfrowania.
     *
     * @return Ścieżka do folderu, w którym chcemy zapisać klucze.
     */
    String selectPath();

    /**
     * Funkcja pobierające hasło z komponentu {@link org.example.app.textFields.api.PasswordInput PasswordInput}.
     *
     * @return Wpisane hasło.
     */
    String collectPassword();

    /**
     * Funkcja tworząca parę kluczy private.key i public.key w wybranej lokalizacji.
     *
     * @param path     Ścieżka do położenia pary kluczy.
     * @param password Hasło do odszyfrowania klucza prywatnego.
     */
    void generateRSA(String path, String password);
}
