package org.example.controller.impl;

import org.example.file.service.api.FileSecurityService;
import org.example.file.service.api.FileService;
import org.example.file.service.impl.FileSecurityServiceDefault;
import org.example.file.service.impl.FileServiceDefault;
import org.example.keyRSA.service.api.RSAService;
import org.example.keyRSA.service.impl.RSAServiceDefault;
import org.example.signXAdES.service.api.SignService;
import org.example.signXAdES.service.impl.SignServiceDefault;

import java.nio.file.attribute.BasicFileAttributes;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Klasa kontrolera aplikacji
 */
public class AppController {
    static private final RSAService rsaService = new RSAServiceDefault();
    static private final SignService signService = new SignServiceDefault();
    static private final FileSecurityService fileSecurityService = new FileSecurityServiceDefault();
    static private final FileService fileService = new FileServiceDefault();

    /**
     * Tworzy i zapisuje klucz prywatny i publiczny w wybranej lokalizacji.
     *
     * @param path     Ścieżka do folderu zapisu kluczy.
     * @param password Hasło do zaszyfrowania klucza prywatnego.
     */
    static public void setRSAKeys(String path, String password) {
        rsaService.createKeys(path, password);
    }

    /**
     * Pobiera klucz prywatny.
     *
     * @param privateKeyPath Ścieżka do pliku z kluczem prywatnym.
     * @param password       Hasło do odszyfrowania klucza prywatnego.
     * @return Zwraca klucz prywatny.
     */
    static public PrivateKey getPrivateKey(String privateKeyPath, String password) {
        return rsaService.encryptPrivateKey(privateKeyPath, password);
    }

    /**
     * Pobiera klucz publiczny.
     *
     * @param publicKeyPath Ścieżka do pliku z kluczem publicznym.
     * @return Zwraca klucz publiczny.
     */
    static public PublicKey getPublicKey(String publicKeyPath) {
        return rsaService.getPublicKey(publicKeyPath);
    }

    /**
     * Podpisuje wybrany plik.
     *
     * @param privateKeyPath Ścieżka do pliku z kluczem prywatnym.
     * @param publicKeyPath  Ścieżka do pliku z kluczem publicznym.
     * @param signedFilePath Ścieżka do pliku, który chcemy podpisać.
     * @param password       Hasło do odszyfrowania klucza prywatnego.
     */
    static public void signFile(String privateKeyPath, String publicKeyPath, String signedFilePath, String password) {

        signService.signXAdES(privateKeyPath, publicKeyPath, signedFilePath, password);
    }

    /**
     * Sprawdza poprawność wybranego podpisu. Podpis musi znajdować się w tym samym folderze co podpisany plik.
     *
     * @param signedFilePath Ścieżka do pliku z podpisem.
     * @return Zwraca true, jeżeli plik i podpis są poprawne.
     */
    static public boolean validateFile(String signedFilePath) {
        return signService.validate(signedFilePath);
    }

    /**
     * Szyfruje wybrany plik.
     *
     * @param privateKeyPath Ścieżka do pliku z kluczem prywatnym.
     * @param password       Hasło do odszyfrowania klucza prywatnego.
     * @param filePath       Ścieżka do pliku, który chcemy zaszyfrować.
     */
    static public void encrypteFile(String privateKeyPath, String password, String filePath) {
        fileSecurityService.encrypte(privateKeyPath, password, filePath);
    }

    /**
     * Odszyfrowuje wybrany plik.
     *
     * @param publicKeyPath Ścieżka do pliku z kluczem publicznym.
     * @param filePath      Ścieżka do pliku, który chcemy odszyfrować.
     */
    static public void decrypteFile(String publicKeyPath, String filePath) {
        fileSecurityService.decrypte(publicKeyPath, filePath);
    }

    /**
     * Zapisuje dane w wybranej lokalizacji.
     *
     * @param filePath Ścieżka, w której zapisujemy plik.
     * @param data     Tablica bajtów, które chcemy zapisać.
     */
    static public void saveFile(String filePath, byte[] data) {
        fileService.save(filePath, data);
    }

    /**
     * Wczytuje wybrany plik.
     *
     * @param filePath Ścieżka do pliku, który odczytujemy.
     * @return Zawartość załadowanego pliku
     */
    static public byte[] loadFile(String filePath) {
        return fileService.load(filePath);
    }

    /**
     * Usuwa wybrany plik.
     *
     * @param filePath Ścieżka do pliku, który usuwamy.
     * @return Zawartość true, jeżeli plik został usunięty.
     */
    static public boolean deleteFile(String filePath) {
        return fileService.delete(filePath);
    }

    /**
     * Pobiera podstawowe metadane wybranego pliku.
     *
     * @param filePath Ścieżka do pliku, z którego odczytujemy metadane.
     * @return Zwraca podstawowe atrybuty pliku.
     */
    static public BasicFileAttributes getFileAttribute(String filePath) {
        return fileService.getFileAttribute(filePath);
    }
}
