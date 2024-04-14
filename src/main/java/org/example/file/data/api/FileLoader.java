package org.example.file.data.api;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Interfejs dostępu do plików.
 */
public interface FileLoader {
    /**
     * Zapisuje dane w wybranej lokalizacji.
     *
     * @param path Ścieżka, w której zapisujemy plik.
     * @param data Tablica bajtów, które chcemy zapisać.
     */
    void saveFile(String path, byte[] data);

    /**
     * Pobiera podstawowe metadane wybranego pliku.
     *
     * @param path Ścieżka do pliku, z którego odczytujemy metadane.
     * @return Zwraca podstawowe atrybuty pliku.
     */
    BasicFileAttributes getFileAttribute(String path) throws IOException;

    /**
     * Wczytuje wybrany plik.
     *
     * @param path Ścieżka do pliku, który odczytujemy.
     * @return Zawartość załadowanego pliku
     */
    byte[] loadFile(String path) throws IOException;

    /**
     * Usuwa wybrany plik.
     *
     * @param path Ścieżka do pliku, który usuwamy.
     * @return Zawartość true, jeżeli plik został usunięty.
     */
    boolean deleteFile(String path) throws IOException;
}
