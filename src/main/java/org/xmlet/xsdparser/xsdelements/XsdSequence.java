package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdSequenceVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * A class representing the xsd:sequence element. Since it shares the same attributes as {@link XsdAll} or
 * {@link XsdChoice} it extends {@link XsdMultipleElements}. For more information check {@link XsdMultipleElements}.
 *
 * @see <a href="https://www.w3schools.com/xml/el_sequence.asp">xsd:sequence description and usage at w3c</a>
 */
public class XsdSequence extends XsdMultipleElements{

    public static final String XSD_TAG = "xsd:sequence";
    public static final String XS_TAG = "xs:sequence";

    /**
     * {@link XsdSequenceVisitor} instance which restricts the children type to {@link XsdElement}, {@link XsdGroup},
     * {@link XsdSequence} or {@link XsdChoice}.
     * Can also have {@link XsdAnnotation} as children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
     */
    private XsdSequenceVisitor visitor = new XsdSequenceVisitor(this);

    /**
     * Specifies the minimum number of times this element can occur in the parent element. The value can be any
     * number bigger or equal to 0. Default value is 1. This attribute cannot be used if the parent element is the
     * XsdSchema element.
     */
    private Integer minOccurs;

    /**
     * Specifies the maximum number of times this element can occur in the parent element. The value can be any
     * number bigger or equal to 0, or if you want to set no limit on the maximum number, use the value "unbounded".
     * Default value is 1. This attribute cannot be used if the parent element is the XsdSchema element.
     */
    private String maxOccurs;

    private XsdSequence(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    /**
     * Sets the occurs fields with the information provided in the Map object or with their default values.
     * @param elementFieldsMapParam The Map object containing the information previously contained in the Node object.
     */
    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.minOccurs = AttributeValidations.validateNonNegativeInteger(XSD_TAG, MIN_OCCURS_TAG, elementFieldsMap.getOrDefault(MIN_OCCURS_TAG, "1"));
        this.maxOccurs = AttributeValidations.maxOccursValidation(XSD_TAG, elementFieldsMap.getOrDefault(MAX_OCCURS_TAG, "1"));
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public XsdSequenceVisitor getVisitor() {
        return visitor;
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node){
        return xsdParseSkeleton(node, new XsdSequence(parser, convertNodeMap(node.getAttributes())));
    }

    @SuppressWarnings("unused")
    public Integer getMinOccurs() {
        return minOccurs;
    }

    @SuppressWarnings("unused")
    public String getMaxOccurs() {
        return maxOccurs;
    }
}
