package org.xmlet.xsdparser.core;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmlet.xsdparser.xsdelements.XsdSchema;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link XsdParser} in the core class of the XsdParser project. It functions as a one shot class, receiving the name
 * of the file to parse in its constructor and storing the parse results in its multiple fields, which can be consulted
 * after the instance is created.
 */
public class XsdParser extends XsdParserCore{

    /**
     * The XsdParser constructor will parse the XSD file with the {@code filepath} and will also parse all the subsequent
     * XSD files with their path present in xsd:import and xsd:include tags. After parsing all the XSD files present it
     * resolves the references existent in the XSD language, represented by the ref attribute. When this method finishes
     * the parse results and remaining unsolved references are accessible by the {@link XsdParser#getResultXsdSchemas()},
     * {@link XsdParser#getResultXsdElements()} and {@link XsdParser#getUnsolvedReferences()}.
     * @param filePath States the path of the XSD file to be parsed.
     */
    public XsdParser(String filePath){
        schemaLocations.add(filePath);
        int index = 0;

        while (schemaLocations.size() > index){
            String schemaLocation = schemaLocations.get(index);
            parseFile(schemaLocation);
            ++index;
        }

        resolveRefs();
    }

    /**
     * Parses a XSD file and all its containing XSD elements. This code iterates on the nodes and parses the supported
     * ones. The supported types are all the XSD types that have their tag present in the {@link XsdParser#parseMappers}
     * field.
     * @param filePath The path to the XSD file.
     */
    private void parseFile(String filePath) {
        //https://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
        try {
            if (!new File(filePath).exists()){
                throw new FileNotFoundException();
            }

            Node schemaNode = getSchemaNode(filePath);

            if (isXsdSchema(schemaNode)){
                XsdSchema.parse(this, schemaNode);
            } else {
                throw new ParsingException("The top level element of a XSD file should be the xsd:schema node.");
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Exception while parsing.", e);
        }
    }

    /**
     * This function uses DOM to obtain a list of nodes from a XSD file.
     * @param filePath The path to the XSD file.
     * @throws IOException If the file parsing throws {@link IOException}.
     * @throws SAXException if the file parsing throws {@link SAXException}.
     * @throws ParserConfigurationException If the {@link DocumentBuilderFactory#newDocumentBuilder()} throws
     *      {@link ParserConfigurationException}.
     * @return A list of nodes that represent the node tree of the XSD file with the path received.
     */
    private Node getSchemaNode(String filePath) throws IOException, SAXException, ParserConfigurationException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filePath);

        doc.getDocumentElement().normalize();

        return doc.getFirstChild();
    }
}