package org.example.app.buttons.impl;

import org.example.app.buttons.api.ValidateRSAKeys;
import org.example.app.textFields.api.PasswordInput;
import org.example.misc.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.security.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Klasa opisująca działanie przycisku do sprawdzania poprawności kluczy RSA.
 */
public class ValidateRSAKeysButton extends ShowRSAKeysButton implements ValidateRSAKeys, Logger, ActionListener {

    /**
     * Konstruktor przycisku do walidacji pary kluczy RSA.
     *
     * @param passwordComponent Komponent przechowujący informacje o haśle.
     * @param listener          Kolekcja komponentów nasłuchujących akcje.
     */
    public ValidateRSAKeysButton(PasswordInput passwordComponent, PropertyChangeListener... listener) {
        super(passwordComponent);
        setText("Sprawdź poprwność pary kluczy");
        this.addActionListener(this);
        for (PropertyChangeListener pcl : listener) {
            this.addPropertyChangeListener("log", pcl);
            this.addPropertyChangeListener("clear", pcl);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.firePropertyChange("clear", false, true);
        String privateKeyPath = selectPrivateKeyPath();
        String publicKeyPath = selectPublicKeyPath();
        String password = collectPassword();
        KeyPair keyPair = getKeyPair(privateKeyPath, publicKeyPath, password);
        boolean val = validateKeys(keyPair);
        printLog("Czy klucze są parą: " + (val ? "Tak" : "Nie"));
    }

    @Override
    public boolean validateKeys(KeyPair keyPair) {
        try {
            byte[] challenge = new byte[1000];
            ThreadLocalRandom.current().nextBytes(challenge);
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(keyPair.getPrivate());
            sig.update(challenge);
            byte[] signature = sig.sign();

            sig.initVerify(keyPair.getPublic());
            sig.update(challenge);
            return sig.verify(signature);
        } catch (NoSuchAlgorithmException e) {
            printLog("Nie znaleziono algorytmu szyfrującego");
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            printLog("Klucz jest niepoprawny");
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }
}
