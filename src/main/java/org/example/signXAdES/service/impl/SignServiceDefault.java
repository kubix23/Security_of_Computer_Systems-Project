package org.example.signXAdES.service.impl;

import org.example.controller.impl.AppController;
import org.example.signXAdES.functions.XAdESsignFunction;
import org.example.signXAdES.functions.XadESValidatorFunction;
import org.example.signXAdES.service.api.SignService;
import org.xml.sax.SAXException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Klasa serwisu implementująca zarządzanie podpisami XAdES.
 */
public class SignServiceDefault implements SignService {
    @Override
    public void signXAdES(String privateKeyPath, String publicKeyPath, String signedFilePath, String password) {
        try {
            PublicKey publicKey = AppController.getPublicKey(publicKeyPath);
            PrivateKey privateKey = AppController.getPrivateKey(privateKeyPath, password);
            BasicFileAttributes basicFileAttributes = AppController.getFileAttribute(signedFilePath);
            byte[] signFile = new XAdESsignFunction(
                    publicKey,
                    privateKey,
                    signedFilePath,
                    basicFileAttributes
            ).getSignFile();
            AppController.saveFile(signedFilePath + ".xades", signFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean validate(String signedFilePath) {
        try {
            byte[] signedFile = AppController.loadFile(signedFilePath);
            return new XadESValidatorFunction(signedFile, signedFilePath).getValidate();
        } catch (IOException | SAXException | MarshalException | XMLSignatureException | ParserConfigurationException |
                 NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }
}
