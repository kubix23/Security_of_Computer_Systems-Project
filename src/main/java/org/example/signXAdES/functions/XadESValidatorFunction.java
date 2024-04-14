package org.example.signXAdES.functions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.crypto.*;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;

/**
 * Klasa implementująca metody weryfikacji podpisu XAdES.
 */
public class XadESValidatorFunction {
    Boolean isValidate;

    /**
     * Konstruktor tworzy klasę {@link Document Dokument} i sprawdza poprawność podpisu.
     *
     * @param signedFilePath Ścieżka do pliku, który chcemy podpisać.
     * @param signedFile     Tablica bajtów podpisanego pliku.
     */
    public XadESValidatorFunction(byte[] signedFile, String signedFilePath)
            throws IOException, SAXException, MarshalException, XMLSignatureException,
            ParserConfigurationException, NoSuchProviderException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document signDoc = docBuilder.parse(new ByteArrayInputStream(signedFile));
        markSignerPropertiesId(signDoc);
        isValidate = validate(signDoc, signedFilePath);
    }

    /**
     * @return Zawraca true, jeżeli podpis i plik są zgodne.
     */
    public Boolean getValidate() {
        return isValidate;
    }

    /**
     * Funkcja oznaczająca wszystkie atrybuty id jako id.
     *
     * @param document Klasa reprezentująca {@link Document Dokument}.
     */
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

    /**
     * Funkcja sprawdzająca poprawność podpisu.
     *
     * @param document       Klasa reprezentująca {@link Document Dokument}.
     * @param signedFilePath Ścieżka do pliku, który chcemy podpisać.
     */
    private boolean validate(Document document, String signedFilePath)
            throws MarshalException, XMLSignatureException, NoSuchProviderException {
        DOMValidateContext validateContext = new DOMValidateContext(new KeyValueSelector(), document);
        validateContext.setProperty("org.jcp.xml.dsig.secureValidation", false);
        validateContext.setDefaultNamespacePrefix("ds");
        validateContext.setBaseURI(Path.of(signedFilePath).getParent().toUri().toString());

        XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM", "XMLDSig");
        XMLSignature signature = xmlSignatureFactory.unmarshalXMLSignature(validateContext);

        return signature.validate(validateContext);
    }

    /**
     * Klasa wybierająca klucz publiczny z podpisu.
     */
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

        /**
         * Funkcja sprawdzająca,  czy{@link XMLStructure} jest {@link KeyValue}
         * @param keyInfoItem {@link XMLStructure}
         * @return Klucz publiczny.
         */
        private PublicKey findPublicKey(XMLStructure keyInfoItem) {
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
