package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * States the number of fraction digits allowed in a numeric type. The value is defined as an Integer.
 */
public class XsdFractionDigits extends XsdIntegerRestrictions {

    public static final String XSD_TAG = "xsd:fractionDigits";
    public static final String XS_TAG = "xs:fractionDigits";

    private XsdFractionDigits(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void accept(XsdAbstractElementVisitor xsdAbstractElementVisitor) {
        super.accept(xsdAbstractElementVisitor);
        xsdAbstractElementVisitor.visit(this);
    }

    public static ReferenceBase parse(Node node){
        return ReferenceBase.createFromXsd(new XsdFractionDigits(XsdAbstractElement.convertNodeMap(node.getAttributes())));
    }
}
