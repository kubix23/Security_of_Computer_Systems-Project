package org.example.app.buttons.api;

import java.security.KeyPair;

/**
 * Interfejs opisująca działanie komponentów do sprawdzania poprawności kluczy RSA.
 */
public interface ValidateRSAKeys extends ShowRSAKeys {
    /**
     * Funkcja sprawdzająca zgodność klucza prywatnego i publicznego.
     *
     * @param keyPair Para kluczy publiczny i prywatny.
     * @return Zwraca true, jeżeli para kluczy jest zgodna?
     */
    boolean validateKeys(KeyPair keyPair);
}
