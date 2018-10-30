package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * A class representing the xsd:import element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_import.asp">xsd:import description and usage at w3c</a>
 */
public class XsdImport extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:import";
    public static final String XS_TAG = "xs:import";

    /**
     * {@link XsdAnnotatedElementsVisitor} instance which restricts his children to {@link XsdAnnotation}.
     */
    private XsdAnnotatedElementsVisitor visitor = new XsdAnnotatedElementsVisitor(this);

    /**
     * Specifies the a namespace to import.
     */
    private String namespace;

    /**
     * Specifies the URI to the schema for the imported namespace.
     * In this project this attribute is used to specify another file location that contains more element definitions
     * that belong to the same XSD language definition.
     */
    private String schemaLocation;

    private XsdImport(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap) {
        super(parser, attributesMap);

        this.namespace = attributesMap.getOrDefault(NAMESPACE, namespace);
        this.schemaLocation = attributesMap.getOrDefault(SCHEMA_LOCATION, schemaLocation);

        if (this.schemaLocation != null){
            parser.addFileToParse(this.schemaLocation);
        }
    }

    @Override
    public XsdAbstractElementVisitor getVisitor() {
        return visitor;
    }

    public static ReferenceBase parse(@NotNull XsdParserCore parser, Node node){
        return xsdParseSkeleton(node, new XsdImport(parser, convertNodeMap(node.getAttributes())));
    }

    @SuppressWarnings("unused")
    public String getNamespace() {
        return namespace;
    }

    @SuppressWarnings("unused")
    public String getSchemaLocation() {
        return schemaLocation;
    }
}
