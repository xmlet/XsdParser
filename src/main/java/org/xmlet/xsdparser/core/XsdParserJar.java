package org.xmlet.xsdparser.core;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlet.xsdparser.core.utils.ConfigEntryData;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.core.utils.ParserConfig;
import org.xmlet.xsdparser.xsdelements.XsdSchema;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

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
        parse(jarPath, filePath);
    }

    /**
     * Adds the jar file represented the jarPath to the classpath and proceeds by parsing the file present in the
     * previous jar with the path filePath.
     * @param jarPath The path to the jar file.
     * @param filePath The filePath of the XSD file to parse. Relative to the Jar structure.
     * @param config Config for the parser.
     */
    public XsdParserJar(String jarPath, String filePath, ParserConfig config){
        super.updateConfig(config);

        parse(jarPath, filePath);
    }

    private void parse(String jarPath, String filePath){
        setClassLoader(jarPath);

        parseJarFile(filePath);

        int index = 0;

        while (schemaLocations.size() > index){
            String schemaLocation = schemaLocations.get(index);
            parseJarFile(schemaLocation);
            ++index;
        }

        resolveRefs();
    }

    /**
     * Parses the XSD file represented by the received InputStream.
     * @param filePath The filePath of the XSD file.
     */
    private void parseJarFile(String filePath) {
        //https://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
        this.currentFile = filePath.replace("\\", "/");
        InputStream inputStream = classLoader.getResourceAsStream(filePath);

        try {
//            Node schemaNode = getSchemaNode(inputStream);
//
//            if (isXsdSchema(schemaNode)){
//                ConfigEntryData xsdSchemaConfig = parseMappers.getOrDefault(XsdSchema.XSD_TAG, parseMappers.getOrDefault(XsdSchema.XS_TAG, null));
//
//                if (xsdSchemaConfig == null){
//                    throw new ParserConfigurationException("XsdSchema not correctly configured.");
//                }
//
//                xsdSchemaConfig.parserFunction.apply(new ParseData(this, schemaNode, xsdSchemaConfig.visitorFunction));
//            } else {
//                throw new ParsingException("The top level element of a XSD file should be the xsd:schema node.");
//            }

            ConfigEntryData xsdSchemaConfig = parseMappers.getOrDefault(XsdSchema.XSD_TAG, parseMappers.getOrDefault(XsdSchema.XS_TAG, null));

            if (xsdSchemaConfig == null){
                throw new ParserConfigurationException("XsdSchema not correctly configured.");
            }

            ReferenceBase schemaReference = xsdSchemaConfig.parserFunction.apply(new ParseData(this, getSchemaNode(inputStream), xsdSchemaConfig.visitorFunction));
        } catch (SAXException | IOException | ParserConfigurationException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Exception while parsing.", e);
            throw new RuntimeException(e);
        }
    }

    private Node getSchemaNode(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        Document doc = getDocumentBuilder().parse(inputStream);

        doc.getDocumentElement().normalize();

        NodeList nodes = doc.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (isXsdSchema(node)){
                return node;
            }
        }

        throw new ParsingException("The top level element of a XSD file should be the xsd:schema node.");
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