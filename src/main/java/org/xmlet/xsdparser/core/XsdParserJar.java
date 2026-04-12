package org.xmlet.xsdparser.core;

import static java.lang.String.format;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * use {@link XsdParser#fromURL(URL, ParserConfig)} instead
 */
@Deprecated
public class XsdParserJar extends XsdParserCore {

    /**
     * Adds the jar file represented the jarPath to the classpath and proceeds by parsing the file present in the
     * previous jar with the path filePath.
     * @param jarFile The path to the jar file.
     * @param filePath The filePath of the XSD file to parse. Relative to the Jar structure.
     */
	public XsdParserJar(URL jarFile, String filePath) {
		try {
			parse(jarFile, filePath);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

    /**
     * Adds the jar file represented the jarPath to the classpath and proceeds by parsing the file present in the
     * previous jar with the path filePath.
     * @param jarPath The path to the jar file.
     * @param filePath The filePath of the XSD file to parse. Relative to the Jar structure.
     * @param config Config for the parser.
     */
	public XsdParserJar(URL jarPath, String filePath, ParserConfig config) {
		super.updateConfig(config);
		try {
			parse(jarPath, filePath);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void parse(URL jarPath, String filePath) throws IOException {
		schemaLocations.add(new URL(format("jar:%s!/%s", jarPath.toString(), filePath)));
		int index = 0;
		while (schemaLocations.size() > index) {
			URL schemaLocation = schemaLocations.get(index);
			parseFile(schemaLocation);
			++index;
		}
		resolveRefs();
	}

    /**
     * Parses the XSD file represented by the received InputStream.
     * @param url The filePath of the XSD file.
     */
	private void parseFile(URL url) throws IOException {
		this.currentFile = url;
		try {
			ConfigEntryData xsdSchemaConfig = getParseMappers(XsdSchema.TAG);
			if (xsdSchemaConfig == null) {
				throw new ParserConfigurationException("XsdSchema not correctly configured.");
			}
			ReferenceBase schemaReference = xsdSchemaConfig.parserFunction.apply(new ParseData(this, getSchemaNode(url), xsdSchemaConfig.visitorFunction));
			((XsdSchema)schemaReference.getElement()).setFilePath(url);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "Exception while parsing.", e);
			throw new RuntimeException(e);
		}
	}

	private Node getSchemaNode(URL url) throws IOException, SAXException, ParserConfigurationException {
		Document doc = getDocumentBuilder().parse(url.openStream());
		doc.getDocumentElement().normalize();
		NodeList nodes = doc.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (isXsdSchema(node)) {
				return node;
			}
		}
		throw new ParsingException("The top level element of a XSD file should be the xsd:schema node.");
	}

}