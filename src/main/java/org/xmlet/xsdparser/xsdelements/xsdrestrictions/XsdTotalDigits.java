package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.Map;

public class XsdTotalDigits extends XsdAbstractRestrictionChild{

    public static String XSD_TAG = "xsd:totalDigits";
    public static String XS_TAG = "xs:totalDigits";

    private boolean fixed;
    private int value;

    private XsdTotalDigits(int value, boolean fixed){
        this.value = value;
        this.fixed = fixed;
    }

    private XsdTotalDigits(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            fixed = Boolean.parseBoolean(elementFieldsMap.getOrDefault(FIXED, "false"));
            value = Integer.parseInt(elementFieldsMap.getOrDefault(VALUE, "0"));
        }
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    public static ReferenceBase parse(Node node){
        return ReferenceBase.createFromXsd(new XsdTotalDigits(convertNodeMap(node.getAttributes())));
    }

    @Override
    public XsdTotalDigits clone(Map<String, String> placeHolderAttributes) {
        return new XsdTotalDigits(this.value, this.fixed);
    }

    public int getValue() {
        return value;
    }

    public boolean isFixed() {
        return fixed;
    }
}
