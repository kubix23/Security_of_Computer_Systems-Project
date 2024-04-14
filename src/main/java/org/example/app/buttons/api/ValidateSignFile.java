package org.example.app.buttons.api;

/**
 * Interfejs opisująca działanie komponentu do sprawdzania poprawności podpisu i pliku podpisywanego.
 */
public interface ValidateSignFile {
    /**
     * Funkcja wywołująca okienko dialogowe {@link javax.swing.JFileChooser JFileChooser} do wyboru lokalizacji pliku do podpisania.
     *
     * @return Ścieżka do pliku, który chcemy podpisać.
     */
    String selectSignedFilePath();

    /**
     * Funkcja sprawdzająca poprawność podpisu i pliku. Podpis musi znajdować się w tym samym folderze co podpisany plik
     *
     * @param signedFilePath Ścieżka do pliku, który chcemy podpisać.
     * @return Zwraca true, jeżeli podpis jest poprawny?
     */
    boolean validateSign(String signedFilePath);
}
