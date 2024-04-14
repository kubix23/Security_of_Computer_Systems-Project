package org.example.file.service.api;


/**
 * Interfejs z podstawowymi operacjami szyfrowania i deszyfrowania pliku.
 */
public interface FileSecurityService {

    /**
     * Szyfruje wybrany plik.
     *
     * @param privateKeyPath Ścieżka do pliku z kluczem prywatnym.
     * @param password       Hasło do odszyfrowania klucza prywatnego.
     * @param filePath       Ścieżka do pliku, który chcemy zaszyfrować.
     */
    void encrypte(String privateKeyPath, String password, String filePath);

    /**
     * Odszyfrowuje wybrany plik.
     *
     * @param publicKeyPath Ścieżka do pliku z kluczem publicznym.
     * @param filePath      Ścieżka do pliku, który chcemy odszyfrować.
     */
    void decrypte(String publicKeyPath, String filePath);
}
