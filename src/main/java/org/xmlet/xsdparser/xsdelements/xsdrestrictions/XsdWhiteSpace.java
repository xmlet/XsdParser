package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.AttributeValidations;
import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.XsdAnnotatedElements;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.enums.WhiteSpaceEnum;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.function.Function;

/**
 * States how the whiteSpace characters should be treated. The value is defined as an {@link String}.
 */
public class XsdWhiteSpace extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:whiteSpace";
    public static final String XS_TAG = "xs:whiteSpace";
    public static final String TAG = "whiteSpace";

    private boolean fixed;
    private WhiteSpaceEnum value;

    private XsdWhiteSpace(@NotNull XsdParserCore parser, @NotNull Map<String, String> elementFieldsMapParam, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, elementFieldsMapParam, visitorFunction);

        fixed = AttributeValidations.validateBoolean(attributesMap.getOrDefault(FIXED_TAG, "false"));
        String rawValue = elementFieldsMapParam.get(VALUE_TAG);
        if (rawValue == null){
            throw new ParsingException(XSD_TAG + " element: The " + VALUE_TAG + " attribute is required.");
        }
        value = AttributeValidations.belongsToEnum(WhiteSpaceEnum.PRESERVE, rawValue);
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
    public XsdWhiteSpace clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(attributesMap);

        XsdWhiteSpace elementCopy = new XsdWhiteSpace(this.getParser(), placeHolderAttributes, visitorFunction);

        elementCopy.setParent(null);

        return elementCopy;
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdWhiteSpace(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    public boolean isFixed() {
        return fixed;
    }

    public WhiteSpaceEnum getValue() {
        return value;
    }

    public static boolean hasDifferentValue(XsdWhiteSpace o1, XsdWhiteSpace o2) {
        if (o1 == null || o2 == null) {
            return false;
        }

        return !java.util.Objects.equals(o1.getValue(), o2.getValue());
    }
}
