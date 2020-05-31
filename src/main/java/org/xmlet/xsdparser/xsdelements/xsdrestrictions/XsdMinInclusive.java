package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.AttributeValidations;
import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.function.Function;

/**
 * States the minimum value that a given type might take, including the respective value. The value is defined
 * as a {@link String}.
 * Example: If the type has a {@link XsdMinInclusive} of 0 it means that the minimum value it can take is 0.
 */
public class XsdMinInclusive extends XsdStringRestrictions {

    public static final String XSD_TAG = "xsd:minInclusive";
    public static final String XS_TAG = "xs:minInclusive";

    /**
     * Indicates if the value is fixed.
     */
    private boolean fixed;

    private XsdMinInclusive(@NotNull XsdParserCore parser, @NotNull Map<String, String> elementFieldsMapParam, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, elementFieldsMapParam, visitorFunction);

        fixed = AttributeValidations.validateBoolean(attributesMap.getOrDefault(FIXED_TAG, "false"));
    }

    @Override
    public void accept(XsdAbstractElementVisitor xsdAbstractElementVisitor) {
        super.accept(xsdAbstractElementVisitor);
        xsdAbstractElementVisitor.visit(this);
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return ReferenceBase.createFromXsd(new XsdMinInclusive(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    public boolean isFixed() {
        return fixed;
    }
}
