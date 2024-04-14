package org.example.signXAdES.service.api;

/**
 * Interfejs serwisu odpowiedzialny za podpis w formacie XAdES.
 */
public interface SignService {
    /**
     * Podpisuje wybrany plik.
     *
     * @param privateKeyPath Ścieżka do pliku z kluczem prywatnym.
     * @param publicKeyPath  Ścieżka do pliku z kluczem publicznym.
     * @param signedFilePath Ścieżka do pliku, który chcemy podpisać.
     * @param password       Hasło do odszyfrowania klucza prywatnego.
     */
    void signXAdES(String privateKeyPath, String publicKeyPath, String signedFilePath, String password);

    /**
     * Sprawdza poprawność wybranego podpisu. Podpis musi znajdować się w tym samym folderze co podpisany plik.
     *
     * @param signedFilePath Ścieżka do pliku z podpisem.
     * @return Zwraca true, jeżeli plik i podpis są poprawne.
     */
    boolean validate(String signedFilePath);
}
