package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
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

    private XsdImport(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    /**
     * Sets the field with the values present in the Map object and adds the file path present in the
     * {@link XsdImport#schemaLocation} to the {@link XsdParser} parsing queue.
     * @param elementFieldsMapParam The Map object containing the information previously contained in the Node object.
     */
    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.namespace = elementFieldsMap.getOrDefault(NAMESPACE, namespace);
        this.schemaLocation = elementFieldsMap.getOrDefault(SCHEMA_LOCATION, schemaLocation);

        if (this.schemaLocation != null){
            parser.addFileToParse(this.schemaLocation);
        }
    }

    @Override
    public XsdAbstractElementVisitor getVisitor() {
        return visitor;
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node){
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
