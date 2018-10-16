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
 * States the minimum length of a given type, either a {@link String}, a {@link List} or another measurable type. This
 * limit includes the respective value. The value is defined as an {@link Integer}.
 * Example: If the type has a {@link XsdMinLength#value} of 2 it means that the {@link String}, {@link List} or another
 * measurable type should have a minimum length of 2.
 */
public class XsdMinLength extends XsdIntegerRestrictions {

    public static final String XSD_TAG = "xsd:minLength";
    public static final String XS_TAG = "xs:minLength";

    private XsdMinLength(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
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
        return ReferenceBase.createFromXsd(new XsdMinLength(parser, convertNodeMap(node.getAttributes())));
    }
}
