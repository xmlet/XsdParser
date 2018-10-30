package org.xmlet.xsdparser.core;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmlet.xsdparser.xsdelements.XsdSchema;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XsdParserJar extends XsdParserCore {

    private static ClassLoader classLoader;

    /**
     * Adds the jar file represented the jarPath to the classpath and proceeds by parsing the file present in the
     * previous jar with the path filePath.
     * @param jarPath The path to the jar file.
     * @param filePath The filePath of the XSD file to parse. Relative to the Jar structure.
     */
    public XsdParserJar(String jarPath, String filePath){
        setClassLoader(jarPath);

        parseJarFile(classLoader.getResourceAsStream(filePath));

        int index = 0;

        while (schemaLocations.size() > index){
            String schemaLocation = schemaLocations.get(index);
            parseJarFile(classLoader.getResourceAsStream(schemaLocation));
            ++index;
        }

        resolveRefs();
    }

    /**
     * Parses the XSD file represented by the received InputStream.
     * @param inputStream The inputStream of the XSD file.
     */
    private void parseJarFile(InputStream inputStream) {
        //https://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
        try {
            Node schemaNode = getSchemaNode(inputStream);

            if (isXsdSchema(schemaNode)){
                XsdSchema.parse(this, schemaNode);
            } else {
                throw new ParsingException("The top level element of a XSD file should be the xsd:schema node.");
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Exception while parsing.", e);
        }
    }

    private Node getSchemaNode(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);

        doc.getDocumentElement().normalize();

        return doc.getFirstChild();
    }

    /**
     * Creates a new class loader, replacing the current one, having another path added to the classpath. The new
     * path is the path to the jar received in this class constructor.
     * @param jarPath The path of the jar file.
     */
    private void setClassLoader(String jarPath) {
        if (!jarPath.endsWith(".jar")){
            throw new ParsingException("The jarPath received doesn't represent a jar file.");
        }

        ClassLoader originalCl = Thread.currentThread().getContextClassLoader();

        URL url = originalCl.getResource(jarPath);

        if (url == null){
            try {
                url = new URL("file:/" + jarPath);
            } catch (MalformedURLException e) {
                throw new ParsingException("Invalid jar name.");
            }
        }

        // Create class loader using given codebase
        // Use prevCl as parent to maintain current visibility
        ClassLoader urlCl = URLClassLoader.newInstance(new URL[]{url}, originalCl);

        Thread.currentThread().setContextClassLoader(urlCl);

        classLoader = urlCl;
    }
}