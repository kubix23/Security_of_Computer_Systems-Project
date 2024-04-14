package org.example.signXAdES.functions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Klasa implementująca metody tworzące podpis w formacie XAdES.
 */
public class XAdESsignFunction {
    String signatureId;
    String signedPropertiesId;
    String objectReference;
    byte[] signFile;

    /**
     * Konstruktor tworzy klasę {@link Document Dokument}, generuje w niej podpis XAdES i zamienia na tablicę bajtów.
     *
     * @param publicKey          Klucz publiczny.
     * @param privateKey         Klucz prywatny.
     * @param signedFilePath     Ścieżka do pliku, który chcemy podpisać.
     * @param basicFileAttribute Zwraca podstawowe atrybuty pliku.
     */
    public XAdESsignFunction(PublicKey publicKey,
                             PrivateKey privateKey,
                             String signedFilePath,
                             BasicFileAttributes basicFileAttribute
    ) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        Document doc = docFactory.newDocumentBuilder().newDocument();
        Document signDoc = signEnveloped(doc, privateKey, publicKey, Path.of(signedFilePath), basicFileAttribute);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(bos);
        transformer.transform(new DOMSource(signDoc), result);
        signFile = bos.toByteArray();

    }

    /**
     * @return Tablica bajtów podpisu.
     */
    public byte[] getSignFile() {
        return signFile;
    }

    /**
     * Funkcja generująca podpis XAdES.
     *
     * @param sign               Klasa reprezentująca {@link Document Dokument}
     * @param publicKey          Klucz publiczny.
     * @param privateKey         Klucz prywatny.
     * @param signedFilePath     Ścieżka do pliku, który chcemy podpisać.
     * @param basicFileAttribute Zwraca podstawowe atrybuty pliku.
     */
    public Document signEnveloped(Document sign,
                                  PrivateKey privateKey,
                                  PublicKey publicKey,
                                  Path signedFilePath,
                                  BasicFileAttributes basicFileAttribute
    ) throws Exception {
        signatureId = "Signature-" + UUID.randomUUID();
        signedPropertiesId = "SignedProperties-" + UUID.randomUUID();
        objectReference = "Reference-" + UUID.randomUUID();

        XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM", "XMLDSig");
        List<XMLObject> objects = getObjectsFromData(xmlSignatureFactory, sign, signedFilePath, basicFileAttribute);
        SignedInfo signedInfo = createSignedInfo(
                xmlSignatureFactory,
                URLEncoder.encode(
                        signedFilePath.getFileName().toString(), StandardCharsets.UTF_8
                ).replace("+", "%20")
                , "#" + signedPropertiesId
        );
        KeyInfo ki = createKeyInfo(xmlSignatureFactory, publicKey);
        XMLSignature xmlSignature = xmlSignatureFactory.newXMLSignature(signedInfo, ki, objects, signatureId, null);

        DOMSignContext domSignContext = new DOMSignContext(privateKey, sign);
        domSignContext.setDefaultNamespacePrefix("ds");
        domSignContext.setBaseURI(signedFilePath.getParent().toUri().toString());
        xmlSignature.sign(domSignContext);

        return sign;
    }

    /**
     * Funkcja tworzy znacznik z kluczem publicznym.
     *
     * @param xmlSignatureFactory {@link XMLSignatureFactory XMLSignatureFactory}
     * @param publicKey           Klucz publiczny.
     * @return struktura znacznika {@link KeyInfo}.
     */
    private KeyInfo createKeyInfo(XMLSignatureFactory xmlSignatureFactory, PublicKey publicKey) throws Exception {
        KeyInfoFactory keyInfoFactory = xmlSignatureFactory.getKeyInfoFactory();
        return keyInfoFactory.newKeyInfo(List.of(keyInfoFactory.newKeyValue(publicKey)));
    }

    /**
     * Funkcja tworzy znacznik z elementem zawierającym informację, która jest podpisana.
     *
     * @param xmlSignatureFactory {@link XMLSignatureFactory XMLSignatureFactory}
     * @param referenceString     Kolekcja linków URL do podpisywanych komponentów.
     * @return struktura znacznika {@link SignedInfo}.
     */
    private SignedInfo createSignedInfo(XMLSignatureFactory xmlSignatureFactory, String... referenceString)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        CanonicalizationMethod c14nMethod = xmlSignatureFactory.newCanonicalizationMethod(
                CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null
        );
        SignatureMethod signMethod = xmlSignatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA512, null);
        DigestMethod digestMethod = xmlSignatureFactory.newDigestMethod(DigestMethod.SHA256, null);

        ArrayList<Reference> references = new ArrayList<>();
        for (String r : referenceString) {
            String type = r.equals("#" + signedPropertiesId) ? "http://uri.etsi.org/01903#SignedProperties" : null;
            String id = r.equals("#" + signedPropertiesId) ? null : objectReference;
            references.add(xmlSignatureFactory.newReference(
                    r,
                    digestMethod,
                    null,
                    type,
                    id
            ));
        }

        return xmlSignatureFactory.newSignedInfo(c14nMethod, signMethod, references);
    }

    /**
     * Funkcja tworząca listę obiektów xml.
     *
     * @param xmlSignatureFactory {@link XMLSignatureFactory XMLSignatureFactory}
     * @param document            Klasa reprezentująca {@link Document Dokument}
     * @param signedFilePath      Ścieżka do pliku, który chcemy podpisać.
     * @param basicFileAttribute  Zwraca podstawowe atrybuty pliku.
     * @return Lista obiektów {@link XMLObject}.
     */
    private List<XMLObject> getObjectsFromData(XMLSignatureFactory xmlSignatureFactory, Document document,
                                               Path signedFilePath, BasicFileAttributes basicFileAttribute) {
        return List.of(prepareXadesXmlObject(xmlSignatureFactory, document, signedFilePath, basicFileAttribute));
    }

    /**
     * Funkcja tworząca element znacznika <Object>.
     *
     * @param xmlSignFactory     {@link XMLSignatureFactory XMLSignatureFactory}
     * @param document           Klasa reprezentująca {@link Document Dokument}
     * @param signedFilePath     Ścieżka do pliku, który chcemy podpisać.
     * @param basicFileAttribute Zwraca podstawowe atrybuty pliku.
     * @return {@link XMLObject}
     */
    private XMLObject prepareXadesXmlObject(XMLSignatureFactory xmlSignFactory, Document document,
                                            Path signedFilePath, BasicFileAttributes basicFileAttribute) {
        Element qualifyingPropertiesElement = createXadesObject(document, signedFilePath, basicFileAttribute);
        return xmlSignFactory.newXMLObject(
                List.of(new DOMStructure(qualifyingPropertiesElement)),
                null,
                null,
                null
        );
    }

    /**
     * Funkcja tworząca elementy charakterystyczne dla podpisu XAdES.
     *
     * @param doc                Klasa reprezentująca {@link Document Dokument}
     * @param signedFilePath     Ścieżka do pliku, który chcemy podpisać.
     * @param basicFileAttribute Zwraca podstawowe atrybuty pliku.
     * @return Zwraca {@link Element} ze znacznikami podpisu XAdES.
     */
    private Element createXadesObject(Document doc, Path signedFilePath, BasicFileAttributes basicFileAttribute) {
        Element qualifyingProperties = createQualifyingPropertiesObject(doc);
        qualifyingProperties.appendChild(
                prepareSignedPropertiesElement(doc, signedFilePath, basicFileAttribute)
        );
        return qualifyingProperties;
    }

    /**
     * Funkcja tworząca elementy charakterystyczne dla podpisu XAdES.
     * @param doc Klasa reprezentująca {@link Document Dokument}
     * @return Zwraca {@link Element} jako kontener dla elementów XAdES.
     */
    private Element createQualifyingPropertiesObject(Document doc) {
        Element qualifyingProperties = doc.createElement("xades:QualifyingProperties");
        qualifyingProperties.setAttribute("Target", "#" + signatureId);
        qualifyingProperties.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xades", "http://uri.etsi.org/01903/v1.3.2#");
        return qualifyingProperties;
    }

    /**
     * Funkcja tworząca elementy podpisywane w podpisie XAdES.
     *
     * @param doc                Klasa reprezentująca {@link Document Dokument}
     * @param signedFilePath     Ścieżka do pliku, który chcemy podpisać.
     * @param basicFileAttribute Zwraca podstawowe atrybuty pliku.
     * @return Zwraca {@link Element} zawierający dane xml do podpisania.
     */
    private Element prepareSignedPropertiesElement(Document doc, Path signedFilePath, BasicFileAttributes basicFileAttribute) {
        Element signedProperties = doc.createElement("xades:SignedProperties");
        signedProperties.setAttribute("Id", signedPropertiesId);
        signedProperties.setIdAttribute("Id", true);

        Element signedSignatureProperties = prepareSignedSignatureProperties(doc);
        Element signedDataObjectProperties = prepareSignedDataObjectProperties(doc, signedFilePath, basicFileAttribute);

        signedProperties.appendChild(signedSignatureProperties);
        signedProperties.appendChild(signedDataObjectProperties);

        return signedProperties;
    }

    /**
     * Funkcja tworząca znacznik z informacjami o podpisie XAdES.
     * @param doc Klasa reprezentująca {@link Document Dokument}
     * @return Zwraca {@link Element} zawierający informacje podpisu XAdES.
     */
    private Element prepareSignedSignatureProperties(Document doc) {
        Element signedSignatureProperties = doc.createElement("xades:SignedSignatureProperties");
        signedSignatureProperties.appendChild(prepareSigningTimeProperties(doc));
        signedSignatureProperties.appendChild(prepareSignerRoleProperties(doc));

        return signedSignatureProperties;
    }

    /**
     * Funkcja tworząca znacznik z danymi podpisywanego pliku.
     *
     * @param doc                Klasa reprezentująca {@link Document Dokument}
     * @param signedFilePath     Ścieżka do pliku, który chcemy podpisać.
     * @param basicFileAttribute Zwraca podstawowe atrybuty pliku.
     * @return Zwraca {@link Element} zawierający dane podpisywanego pliku.
     */
    private Element prepareSignedDataObjectProperties(Document doc, Path signedFilePath, BasicFileAttributes basicFileAttribute) {
        Element signedDataObjectProperties = doc.createElement("xades:SignedDataObjectProperties");
        Element dataObjectFormat = doc.createElement("xades:DataObjectFormat");
        dataObjectFormat.setAttribute("ObjectReference", "#" + objectReference);

        dataObjectFormat.appendChild(prepareMimeTypeProperties(doc, signedFilePath));
        dataObjectFormat.appendChild(
                prepareDescriptionProperties(doc, signedFilePath.getFileName().toString(), basicFileAttribute)
        );

        signedDataObjectProperties.appendChild(dataObjectFormat);
        return signedDataObjectProperties;
    }

    /**
     * Funkcja tworząca dane osoby tworzącej podpis.
     *
     * @param doc Klasa reprezentująca {@link Document Dokument}
     * @return Zwraca {@link Element} zawierający dane osoby tworzącej podpis.
     */
    private Element prepareSignerRoleProperties(Document doc) {
        Element signerRole = doc.createElement("xades:SignerRole");
        Element claimedRoles = doc.createElement("xades:ClaimedRoles");
        claimedRoles.appendChild(
                doc.createTextNode(System.getProperty("user.name"))
        );
        signerRole.appendChild(claimedRoles);
        return signerRole;
    }

    /**
     * Funkcja tworząca znacznik z datą podpisania pliku.
     *
     * @param doc Klasa reprezentująca {@link Document Dokument}
     * @return Zwraca {@link Element} z datę podpisania pliku.
     */
    private Element prepareSigningTimeProperties(Document doc) {
        Element signingTime = doc.createElement("xades:SigningTime");
        signingTime.appendChild(
                doc.createTextNode(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()))
        );
        return signingTime;
    }

    /**
     * Funkcja tworząca znacznik z typem mime podpisywanego pliku.
     *
     * @param doc Klasa reprezentująca {@link Document Dokument}
     * @return Zwraca {@link Element} z typem mime podpisywanego pliku.
     */
    private Element prepareMimeTypeProperties(Document doc, Path signedFilePath){
        Element mimeType = doc.createElement("xades:MimeType");
        try {
            mimeType.appendChild(doc.createTextNode(Files.probeContentType(signedFilePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return mimeType;
    }

    /**
     * Funkcja tworząca znacznik z opisem podpisywanego pliku.
     *
     * @param doc Klasa reprezentująca {@link Document Dokument}
     * @return Zwraca {@link Element} z opisem podpisywanego pliku.
     */
    private Element prepareDescriptionProperties(Document doc,String signedFileName, BasicFileAttributes basicFileAttribute){
        Element description = doc.createElement("xades:Description");
        description.appendChild(doc.createTextNode("Size:%d; Filename:%s; Date_of_modification:%s "
                .formatted(
                        basicFileAttribute.size(),
                        signedFileName,
                        basicFileAttribute.lastModifiedTime().toString()
                )));
        return description;
    }
}
