package org.example.file.service.api;

import java.nio.file.attribute.BasicFileAttributes;

/**
 * Interfejs serwisu odpowiedzialny za przekazanie plików.
 */
public interface FileService {
    /**
     * Wczytuje wybrany plik.
     *
     * @param path Ścieżka do pliku, który odczytujemy.
     * @return Zawartość załadowanego pliku
     */
    byte[] load(String path);

    /**
     * Zapisuje dane w wybranej lokalizacji.
     *
     * @param path Ścieżka, w której zapisujemy plik.
     * @param data Tablica bajtów, które chcemy zapisać.
     */
    void save(String path, byte[] data);

    /**
     * Pobiera podstawowe metadane wybranego pliku.
     *
     * @param path Ścieżka do pliku, z którego odczytujemy metadane.
     * @return Zwraca podstawowe atrybuty pliku.
     */
    BasicFileAttributes getFileAttribute(String path);

    /**
     * Usuwa wybrany plik.
     *
     * @param path Ścieżka do pliku, który usuwamy.
     * @return Zawartość true, jeżeli plik został usunięty.
     */
    boolean delete(String path);
}
