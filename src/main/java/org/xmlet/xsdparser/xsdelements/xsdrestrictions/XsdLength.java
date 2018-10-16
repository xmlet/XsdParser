package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.AttributeValidations;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * States the exact number of length to a given type, either a {@link String}, a {@link List}, or another measurable type.
 * The value is defined as an {@link Integer}.
 */
public class XsdLength extends XsdIntegerRestrictions {

    public static final String XSD_TAG = "xsd:length";
    public static final String XS_TAG = "xs:length";

    private XsdLength(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        value = AttributeValidations.validateRequiredNonNegativeInteger(XSD_TAG, VALUE_TAG, elementFieldsMap.get(VALUE_TAG));
    }

    @Override
    public void accept(XsdAbstractElementVisitor xsdAbstractElementVisitor) {
        super.accept(xsdAbstractElementVisitor);
        xsdAbstractElementVisitor.visit(this);
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node){
        return ReferenceBase.createFromXsd(new XsdLength(parser, convertNodeMap(node.getAttributes())));
    }
}
