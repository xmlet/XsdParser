package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.AttributeValidations;
import org.xmlet.xsdparser.xsdelements.XsdAnnotatedElements;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.enums.WhiteSpaceEnum;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * States how the whiteSpace characters should be treated. The value is defined as an {@link String}.
 */
public class XsdWhiteSpace extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:whiteSpace";
    public static final String XS_TAG = "xs:whiteSpace";

    private XsdAnnotatedElementsVisitor visitor = new XsdAnnotatedElementsVisitor(this);

    private boolean fixed;
    private WhiteSpaceEnum value;

    private XsdWhiteSpace(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        fixed = AttributeValidations.validateBoolean(elementFieldsMap.getOrDefault(FIXED_TAG, "false"));
        value = AttributeValidations.belongsToEnum(WhiteSpaceEnum.PRESERVE, elementFieldsMapParam.getOrDefault(VALUE_TAG, null));
    }

    @Override
    public void accept(XsdAbstractElementVisitor xsdAbstractElementVisitor) {
        super.accept(xsdAbstractElementVisitor);
        xsdAbstractElementVisitor.visit(this);
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node){
        return ReferenceBase.createFromXsd(new XsdWhiteSpace(parser, convertNodeMap(node.getAttributes())));
    }

    public boolean isFixed() {
        return fixed;
    }

    @Override
    public XsdAnnotatedElementsVisitor getVisitor() {
        return visitor;
    }

    public WhiteSpaceEnum getValue() {
        return value;
    }

    public static boolean hasDifferentValue(XsdWhiteSpace o1, XsdWhiteSpace o2) {
        if (o1 == null && o2 == null) {
            return false;
        }

        WhiteSpaceEnum o1Value = null;
        WhiteSpaceEnum o2Value;

        if (o1 != null) {
            o1Value = o1.getValue();
        }

        if (o2 != null) {
            o2Value = o2.getValue();
            return o2Value.equals(o1Value);
        }

        return false;
    }
}
