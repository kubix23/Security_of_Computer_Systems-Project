package org.example.keyRSA.data.api;

import java.io.IOException;

/**
 * Interfejs dostępu do kluczy.
 */
public interface KeyLoader {
    /**
     * Zapisuje klucz w wybranej lokalizacji.
     *
     * @param path Ścieżka, w której zapisujemy klucz.
     * @param data Tablica bajtów, które chcemy zapisać.
     */
    void saveKey(String path, byte[] data);

    /**
     * Wczytuje wybrany klucz.
     *
     * @param path Ścieżka do klucza, który odczytujemy.
     * @return Dane wybranego klucza.
     */
    byte[] loadKey(String path) throws IOException;
}
