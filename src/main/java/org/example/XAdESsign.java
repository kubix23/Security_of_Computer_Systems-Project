package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.*;

public class XAdESsign {
    File fileToSign;
    Element signedPropertiesElement;
    String signatureId;
    PublicKey publicKey;
    ArrayList<String> ObjectReference = new ArrayList<>();

    String signedPropertiesId;

    public XAdESsign(String password) throws Exception {
        PrivateKeyDecryptor key = new PrivateKeyDecryptor(password);
        if (key.getPr() != null) {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Wybierz plik do podpisania");
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                docFactory.setNamespaceAware(true);
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.newDocument();
                fileToSign = fc.getSelectedFile();

                Document signDoc = signEnveloped(doc, key.getPr(), fc.getSelectedFile());

                try (FileOutputStream output = new FileOutputStream(
                        String.valueOf(fc.getSelectedFile().toPath().getParent()) +
                                '/' +
                                fc.getSelectedFile().getName() +
                                ".xades"
                )
                ) {

                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(signDoc);

                    StreamResult result = new StreamResult(output);
                    transformer.transform(source, result);
                }
            }
        }

    }

    public Document signEnveloped(Document document, PrivateKey privateKey, File fileToSign)
            throws Exception {

        signatureId = "Signature-" + UUID.randomUUID();
        signedPropertiesId = "SignedProperties-" + UUID.randomUUID();
        ObjectReference.add("Reference-" + UUID.randomUUID());
        ObjectReference.add(null);
        XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM", "XMLDSig");
        List<XMLObject> objects = getObjectsFromData(xmlSignatureFactory, document);
        SignedInfo signedInfo = createSignedInfo(
                xmlSignatureFactory,
                URLEncoder.encode(fileToSign.getName(), StandardCharsets.UTF_8).replace("+", "%20")
                , "#" + signedPropertiesId
        );
        KeyInfo ki = createKeyInfo(xmlSignatureFactory);
        XMLSignature xmlSignature = xmlSignatureFactory.newXMLSignature(signedInfo, ki, objects, signatureId, null);

        DOMSignContext domSignContext = new DOMSignContext(privateKey, document);
        domSignContext.setDefaultNamespacePrefix("ds");
        domSignContext.setBaseURI(fileToSign.toPath().getParent().toUri().toString());

        xmlSignature.sign(domSignContext);

        return document;
    }

    private KeyInfo createKeyInfo(XMLSignatureFactory xmlSignatureFactory) throws Exception {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Wybierz klucz publiczny");
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            PublicKey key;
            try (FileInputStream fos = new FileInputStream(fc.getSelectedFile())) {
                ObjectInputStream o = new ObjectInputStream(fos);
                key = (PublicKey) o.readObject();
                publicKey = key;
            }

            KeyInfoFactory keyInfoFactory = xmlSignatureFactory.getKeyInfoFactory();
            return keyInfoFactory.newKeyInfo(List.of(keyInfoFactory.newKeyValue(key)));
        } else {
            throw new Exception("Zły plik");
        }
    }


    private SignedInfo createSignedInfo(XMLSignatureFactory xmlSignatureFactory, String... referenceString)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        CanonicalizationMethod c14nMethod = xmlSignatureFactory.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null);
        SignatureMethod signMethod = xmlSignatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA512, null);
        DigestMethod digestMethod = xmlSignatureFactory.newDigestMethod(DigestMethod.SHA256, null);

        ArrayList<Reference> references = new ArrayList<>();
        Iterator<String> id = ObjectReference.iterator();
        for (String r : referenceString) {
            String type = r.equals("#" + signedPropertiesId) ? "http://uri.etsi.org/01903#SignedProperties" : null;
            references.add(xmlSignatureFactory.newReference(
                    r,
                    digestMethod,
                    null,
                    type,
                    id.next()
            ));
        }

        return xmlSignatureFactory.newSignedInfo(c14nMethod, signMethod, references);
    }

    private List<XMLObject> getObjectsFromData(XMLSignatureFactory xmlSignatureFactory, Document document) {
        List<XMLObject> objects = new ArrayList<>();
        XMLObject xadesXmlObject = prepareXadesXmlObject(xmlSignatureFactory, document);
        objects.add(xadesXmlObject);
        return objects;
    }

    private XMLObject prepareXadesXmlObject(XMLSignatureFactory xmlSignFactory, Document document) {
        Element qualifyingPropertiesElement = createXadesObject(document);

        return xmlSignFactory.newXMLObject(Collections.singletonList(new DOMStructure(qualifyingPropertiesElement)), null, null, null);
    }

    private Element createXadesObject(Document document) {
        Element qualifyingProperties = createQualifyingPropertiesObject(document);
        Element signedProperties = prepareSignedPropertiesElement(document);
        qualifyingProperties.appendChild(signedProperties);

        return qualifyingProperties;
    }

    private Element createQualifyingPropertiesObject(Document doc) {
        Element qualifyingProperties = doc.createElement("xades:QualifyingProperties");
        qualifyingProperties.setAttribute("Target", "#" + signatureId);
        qualifyingProperties.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xades", "http://uri.etsi.org/01903/v1.3.2#");
        return qualifyingProperties;
    }

    private Element prepareSignedPropertiesElement(Document doc) {
        Element signedProperties = doc.createElement("xades:SignedProperties");
        signedProperties.setAttribute("Id", signedPropertiesId);
        signedProperties.setIdAttribute("Id", true);

        Element signedSignatureProperties = prepareSignedSignatureProperties(doc);
        Element signedDataObjectProperties = prepareSignedDataObjectProperties(doc);

        signedProperties.appendChild(signedSignatureProperties);
        signedProperties.appendChild(signedDataObjectProperties);

        signedPropertiesElement = signedProperties;
        return signedProperties;
    }

    private Element prepareSignedSignatureProperties(Document doc) {
        Element signedSignatureProperties = doc.createElement("xades:SignedSignatureProperties");

        Element signingTime = doc.createElement("xades:SigningTime");
        signingTime.appendChild(doc.createTextNode(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date())));
        signedSignatureProperties.appendChild(signingTime);


        Element signerRole = doc.createElement("xades:SignerRole");
        Element claimedRoles = doc.createElement("xades:ClaimedRoles");
        claimedRoles.appendChild(doc.createTextNode(System.getProperty("user.name")));
        signerRole.appendChild(claimedRoles);
        signedSignatureProperties.appendChild(signerRole);

        return signedSignatureProperties;
    }

    private Element prepareSignedDataObjectProperties(Document doc) {
        Element signedDataObjectProperties = doc.createElement("xades:SignedDataObjectProperties");
        Element dataObjectFormat = doc.createElement("xades:DataObjectFormat");
        dataObjectFormat.setAttribute("ObjectReference", "#" + ObjectReference.getFirst());

        Element mimeType = doc.createElement("xades:MimeType");
        try {
            mimeType.appendChild(doc.createTextNode(Files.probeContentType(fileToSign.toPath())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dataObjectFormat.appendChild(mimeType);

        Element description = doc.createElement("xades:Description");
        description.appendChild(doc.createTextNode("Size:%d; Filename:%s; Date_of_modification:%s "
                .formatted(
                        fileToSign.length(),
                        fileToSign.getName(),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fileToSign.lastModified())
                )));
        dataObjectFormat.appendChild(description);
        signedDataObjectProperties.appendChild(dataObjectFormat);

        return signedDataObjectProperties;
    }
}
