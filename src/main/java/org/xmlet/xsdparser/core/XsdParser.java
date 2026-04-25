package org.xmlet.xsdparser.core;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

/**
 * {@link XsdParser} in the core class of the XsdParser project. It functions as a one shot class, receiving the name
 * of the file to parse in its constructor and storing the parse results in its multiple fields, which can be consulted
 * after the instance is created.
 */
public class XsdParser extends XsdParserCore{
	
	public static XsdParser fromFile(File pathToXsd, ParserConfig config) {
		try {
			return new XsdParser(pathToXsd.toURI().toURL(), config);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static XsdParser fromJar(File jarFile, String xsdInJar, ParserConfig config) {
		try {
			return new XsdParser(new URL(format("jar:%s!/%s", jarFile.toURI().toURL(), xsdInJar)), config);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static XsdParser fromURL(URL jarFile, String xsdInJar, ParserConfig config) {
		try {
			return new XsdParser(new URL(format("jar:%s!/%s", jarFile.toString(), xsdInJar)), config);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static XsdParser fromURL(URL url, ParserConfig config) {
		return new XsdParser(url, config);
	}
	
    private XsdParser(URL filePath, ParserConfig config){
		if (config != null) {
			super.updateConfig(config);
		}
        parse(filePath);
    }

    /**
     * The XsdParser constructor will parse the XSD file with the {@code filepath} and will also parse all the subsequent
     * XSD files with their path present in xsd:import and xsd:include tags. After parsing all the XSD files present it
     * resolves the references existent in the XSD language, represented by the ref attribute. When this method finishes
     * the parse results and remaining unsolved references are accessible by the {@link XsdParser#getResultXsdSchemas()},
     * {@link XsdParser#getResultXsdElements()} and {@link XsdParser#getUnsolvedReferences()}.
     * @param filePath States the path of the XSD file to be parsed.
     */
    public XsdParser(String filePath){
        try {
 			parse(new File(filePath).toURI().toURL());
 		} catch (MalformedURLException e) {
 			throw new RuntimeException(e.getMessage(), e);
 		}
    }

    /**
     * The XsdParser constructor will parse the XSD file with the {@code filepath} and will also parse all the subsequent
     * XSD files with their path present in xsd:import and xsd:include tags. After parsing all the XSD files present it
     * resolves the references existent in the XSD language, represented by the ref attribute. When this method finishes
     * the parse results and remaining unsolved references are accessible by the {@link XsdParser#getResultXsdSchemas()},
     * {@link XsdParser#getResultXsdElements()} and {@link XsdParser#getUnsolvedReferences()}.
     * @param filePath States the path of the XSD file to be parsed.
     * @param config Config for the parser.
     */
    public XsdParser(String filePath, ParserConfig config){
        super.updateConfig(config);

        try {
			parse(new File(filePath).toURI().toURL());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
    }

    private void parse(URL filePath){
    	schemaLocations.add(filePath);
    	int index = 0;
    	while (schemaLocations.size() > index) {
    		URL schemaLocation = schemaLocations.get(index);
    		parseFile(schemaLocation);
    		++index;
    	}
        resolveRefs();
    }

    /**
     * Parses a XSD file and all its containing XSD elements. This code iterates on the nodes and parses the supported
     * ones. The supported types are all the XSD types that have their tag present in the {@link XsdParser#parseMappers}
     * field.
     * @param url The path to the XSD file.
     */
    private void parseFile(URL url) {
        //https://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
        try {
            this.currentFile = url;

            ConfigEntryData xsdSchemaConfig = getParseMappers(XsdSchema.TAG);

            if (xsdSchemaConfig == null){
                throw new ParserConfigurationException("XsdSchema not correctly configured.");
            }

            ReferenceBase schemaReference = xsdSchemaConfig.parserFunction.apply(new ParseData(this, getSchemaNode(url), xsdSchemaConfig.visitorFunction));
            ((XsdSchema)schemaReference.getElement()).setFilePath(url);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "Exception while parsing.", e);
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "Exception while parsing.", e);
			throw new RuntimeException(e);
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
     * @throws URISyntaxException If the {@code filePath} URL cannot be converted to a valid {@link java.net.URI}.
     */
    private Node getSchemaNode(URL filePath) throws IOException, SAXException, ParserConfigurationException, URISyntaxException {
        Document doc = getDocumentBuilder().parse(filePath.openStream());

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
}