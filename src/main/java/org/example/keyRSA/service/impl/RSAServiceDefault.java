package org.example.keyRSA.service.impl;

import org.example.keyRSA.data.api.KeyLoader;
import org.example.keyRSA.data.impl.KeyLoaderDefault;
import org.example.keyRSA.functions.PrivateKeyDecryptorFunction;
import org.example.keyRSA.functions.PublicKeyDecryptorFunction;
import org.example.keyRSA.functions.RSAgeneratorFunction;
import org.example.keyRSA.service.api.RSAService;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Klasa serwisu odpowiedzialny za zarządzanie kluczami.
 */
public class RSAServiceDefault implements RSAService {
    @Override
    public void createKeys(String pathSave, String password) {
        RSAgeneratorFunction rsaGenerator = new RSAgeneratorFunction(password);
        KeyLoader kl = new KeyLoaderDefault();
        kl.saveKey(pathSave + "/public.key", rsaGenerator.getPublicKey());
        kl.saveKey(pathSave + "/private.key", rsaGenerator.getPrivateKey());
    }

    @Override
    public PublicKey getPublicKey(String publicKeyPath) {
        try {
            byte[] publicKey = new KeyLoaderDefault().loadKey(publicKeyPath);
            return new PublicKeyDecryptorFunction(publicKey).getKey();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PrivateKey encryptPrivateKey(String privateKeyPath, String password) {
        try {
            byte[] privateKey = new KeyLoaderDefault().loadKey(privateKeyPath);
            return new PrivateKeyDecryptorFunction(password, privateKey).getPr();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
