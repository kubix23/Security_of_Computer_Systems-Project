package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.crypto.*;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.security.KeyException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class XadESValidator {
    File fileSigned;

    Boolean isValidate;

    public XadESValidator() throws IOException, SAXException, MarshalException, XMLSignatureException, ParserConfigurationException, NoSuchProviderException {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Wybierz plik podpisu");
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document signDoc = docBuilder.parse(fc.getSelectedFile());

            markSignerPropertiesId(signDoc);
            fileSigned = fc.getSelectedFile();

            isValidate = validate(signDoc);
        }
    }

    public Boolean getValidate() {
        return isValidate;
    }

    private void markSignerPropertiesId(Document document) {
        NodeList signedPropertiesNodeList = document.getElementsByTagName("xades:SignedProperties");
        java.util.Objects.requireNonNull(signedPropertiesNodeList);
        for (int i = 0; i < signedPropertiesNodeList.getLength(); i++) {
            Node node = signedPropertiesNodeList.item(i);
            if (node instanceof Element element) {
                element.setIdAttribute("Id", true);
            }
        }
    }

    private String createXMLDSigValidationErrorMessage(
            DOMValidateContext validateContext,
            XMLSignature signature
    ) throws XMLSignatureException {
        Map<String, Boolean> components = new LinkedHashMap<>();
        validateContext.setProperty("org.jcp.xml.dsig.secureValidation", false);

        boolean signatureValidity = signature.getSignatureValue().validate(validateContext);
        components.put("signature", signatureValidity);

        for (Reference reference : signature.getSignedInfo().getReferences()) {
            String referenceUri = reference.getURI();
            boolean referenceValidity = reference.validate(validateContext);
            System.out.println(new String(Base64.getEncoder().encode(reference.getDigestValue())));
            System.out.println(new String(Base64.getEncoder().encode(reference.getCalculatedDigestValue())));
            String name = "reference[uri=%s]".formatted(referenceUri);
            components.put(name, referenceValidity);
        }

        return components.entrySet().stream()
                .map(e -> "%s validity: %b".formatted(e.getKey(), e.getValue()))
                .collect(Collectors.joining("\n"));
    }


    private boolean validate(Document document) throws MarshalException, XMLSignatureException, NoSuchProviderException {
        DOMValidateContext validateContext = new DOMValidateContext(new KeyValueSelector(), document);
        validateContext.setProperty("org.jcp.xml.dsig.secureValidation", false);
        validateContext.setDefaultNamespacePrefix("ds");
        validateContext.setBaseURI(fileSigned.toPath().getParent().toUri().toString());

        // Create a DOM XMLSignatureFactory that will be used to unmarshal the
        // document containing the XMLSignature
        XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM", "XMLDSig");
        XMLSignature signature = xmlSignatureFactory.unmarshalXMLSignature(validateContext);

        if (!signature.validate(validateContext)) {
            String msg = createXMLDSigValidationErrorMessage(validateContext, signature);
            System.out.println(msg);
        }
        return signature.validate(validateContext);
    }

    private static class KeyValueSelector extends KeySelector {
        public KeySelectorResult select(
                KeyInfo keyInfo,
                Purpose purpose,
                AlgorithmMethod method,
                XMLCryptoContext context
        ) throws KeySelectorException {

            for (XMLStructure keyInfoItem : keyInfo.getContent()) {
                PublicKey publicKey = findPublicKey(keyInfoItem);
                if (publicKey == null) continue;
                return () -> publicKey;
            }

            throw new KeySelectorException("No KeyValue element found!");
        }

        private PublicKey findPublicKey(XMLStructure keyInfoItem) {
            // The <KeyInfo> element can contain different structures holding a
            // public key. In that case a different key-obtaining algorithm would
            // have to be used.
            if (keyInfoItem instanceof KeyValue pk) {
                try {
                    return pk.getPublicKey();
                } catch (KeyException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }
    }
}
