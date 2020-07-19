package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.AttributeValidations;
import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.function.Function;

/**
 * States the minimum value that a given type might take, excluding the respective value. The value is defined
 * as a {@link String}.
 * Example: If the type has a {@link XsdMinExclusive#value} of 0 it means that the minimum value it can take is any
 * value bigger than 0.
 */
public class XsdMinExclusive extends XsdStringRestrictions {

    public static final String XSD_TAG = "xsd:minExclusive";
    public static final String XS_TAG = "xs:minExclusive";

    /**
     * Indicates if the value is fixed.
     */
    private boolean fixed;

    private XsdMinExclusive(@NotNull XsdParserCore parser, @NotNull Map<String, String> elementFieldsMapParam, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, elementFieldsMapParam, visitorFunction);

        fixed = AttributeValidations.validateBoolean(attributesMap.getOrDefault(FIXED_TAG, "false"));
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
    public XsdMinExclusive clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(attributesMap);

        XsdMinExclusive elementCopy = new XsdMinExclusive(this.getParser(), placeHolderAttributes, visitorFunction);

        elementCopy.setParent(null);

        return elementCopy;
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return ReferenceBase.createFromXsd(new XsdMinExclusive(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    public boolean isFixed() {
        return fixed;
    }
}
