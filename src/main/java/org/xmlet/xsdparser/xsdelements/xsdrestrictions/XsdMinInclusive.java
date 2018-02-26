package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.Map;

public class XsdMinInclusive extends XsdAbstractRestrictionChild {

    public static String XSD_TAG = "xsd:minInclusive";
    public static String XS_TAG = "xs:minInclusive";

    private boolean fixed;
    private int value;

    private XsdMinInclusive(int value, boolean fixed){
        this.value = value;
        this.fixed = fixed;
    }

    private XsdMinInclusive(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            fixed = Boolean.parseBoolean(elementFieldsMap.getOrDefault(XsdAbstractElement.FIXED, "false"));
            value = Integer.parseInt(elementFieldsMap.getOrDefault(XsdAbstractElement.VALUE, "0"));
        }
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    public static ReferenceBase parse(Node node){
        return ReferenceBase.createFromXsd(new XsdMinInclusive(XsdAbstractElement.convertNodeMap(node.getAttributes())));
    }

    @Override
    public XsdMinInclusive clone(Map<String, String> placeHolderAttributes) {
        return new XsdMinInclusive(this.value, this.fixed);
    }

    public int getValue() {
        return value;
    }

    public boolean isFixed() {
        return fixed;
    }
}
