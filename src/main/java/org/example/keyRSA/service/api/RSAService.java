package org.example.keyRSA.service.api;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Interfejs serwisu odpowiedzialny za klucze.
 */
public interface RSAService {
    /**
     * Tworzy i zapisuje klucz prywatny i publiczny w wybranej lokalizacji.
     *
     * @param pathSave Ścieżka do folderu zapisu kluczy.
     * @param password Hasło do zaszyfrowania klucza prywatnego.
     */
    void createKeys(String pathSave, String password);

    /**
     * Pobiera klucz publiczny.
     *
     * @param publicKeyPath Ścieżka do pliku z kluczem publicznym.
     * @return Zwraca klucz publiczny.
     */
    PublicKey getPublicKey(String publicKeyPath);

    /**
     * Pobiera klucz prywatny.
     *
     * @param privateKeyPath Ścieżka do pliku z kluczem prywatnym.
     * @param password       Hasło do odszyfrowania klucza prywatnego.
     * @return Zwraca klucz prywatny.
     */
    PrivateKey encryptPrivateKey(String privateKeyPath, String password);
}
