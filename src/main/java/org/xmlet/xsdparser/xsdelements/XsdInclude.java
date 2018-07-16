package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;

import java.util.Map;

/**
 * A class representing the xsd:include element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_include.asp">xsd:include description and usage at w3c</a>
 */
public class XsdInclude extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:include";
    public static final String XS_TAG = "xs:include";

    /**
     * {@link XsdAnnotatedElementsVisitor} instance which restricts his children to {@link XsdAnnotation}.
     */
    private XsdAnnotatedElementsVisitor visitor = new XsdAnnotatedElementsVisitor(this);

    /**
     * Specifies the URI to the schema for the imported namespace.
     * In this project this attribute is used to specify another file location that contains more element definitions
     * that belong to the same XSD language definition.
     */
    private String schemaLocation;

    private XsdInclude(Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    /**
     * Sets the field with the values present in the Map object and adds the file path present in the
     * {@link XsdImport#schemaLocation} to the {@link XsdParser} parsing queue.
     * @param elementFieldsMapParam The Map object containing the information previously contained in the Node object.
     */
    @Override
    public void setFields(Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.schemaLocation = elementFieldsMap.getOrDefault(SCHEMA_LOCATION, schemaLocation);

        if (this.schemaLocation != null){
            XsdParser.getInstance().addFileToParse(this.schemaLocation);
        }
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdInclude(convertNodeMap(node.getAttributes())));
    }

    @Override
    public XsdAbstractElementVisitor getVisitor() {
        return visitor;
    }

    @SuppressWarnings("unused")
    public String getSchemaLocation() {
        return schemaLocation;
    }
}
