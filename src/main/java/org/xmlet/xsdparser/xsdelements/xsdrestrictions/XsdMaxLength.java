package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.AttributeValidations;
import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * States the maximum length of a given type, either a {@link String}, a {@link List} or another measurable type.
 * This limit including the respective value. The value is defined as an {@link Integer}.
 * Example: If the type has a {@link XsdMaxLength#value} of 5 it means that the {@link String}, {@link List} or another
 * measurable type should have a maximum length of 5.
 */
public class XsdMaxLength extends XsdIntegerRestrictions {

    public static final String XSD_TAG = "xsd:maxLength";
    public static final String XS_TAG = "xs:maxLength";
    public static final String TAG = "maxLength";

    private XsdMaxLength(@NotNull XsdParserCore parser, @NotNull Map<String, String> elementFieldsMapParam, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, elementFieldsMapParam, visitorFunction);

        value = AttributeValidations.validateRequiredNonNegativeInteger(XSD_TAG, VALUE_TAG, attributesMap.get(VALUE_TAG));
    }

    @Override
    public void accept(XsdAbstractElementVisitor xsdAbstractElementVisitor) {
        super.accept(xsdAbstractElementVisitor);
        xsdAbstractElementVisitor.visit(this);
    }

    /**
     * Performs a copy of the current object for replacing purposes. The cloned objects are used to replace
     * {@link UnsolvedReference} objects in the reference solving process.
     * @param placeHolderAttributes The additional attributes to add to the clone.
     * @return A copy of the object from which is called upon.
     */
    @Override
    public XsdMaxLength clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(attributesMap);

        XsdMaxLength elementCopy = new XsdMaxLength(this.getParser(), placeHolderAttributes, visitorFunction);

        elementCopy.setParent(null);

        return elementCopy;
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdMaxLength(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }
}
