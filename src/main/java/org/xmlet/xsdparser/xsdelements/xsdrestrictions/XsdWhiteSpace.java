package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.Map;

public class XsdWhiteSpace extends XsdAbstractRestrictionChild {

    public static String XSD_TAG = "xsd:whiteSpace";
    public static String XS_TAG = "xs:whiteSpace";

    private boolean fixed;
    private String value;

    private XsdWhiteSpace(String value, boolean fixed){
        this.value = value;
        this.fixed = fixed;
    }

    private XsdWhiteSpace(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            fixed = Boolean.parseBoolean(elementFieldsMap.getOrDefault(FIXED, "false"));
            value = elementFieldsMap.getOrDefault(VALUE, value);
        }
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    public static ReferenceBase parse(Node node){
        return ReferenceBase.createFromXsd(new XsdWhiteSpace(convertNodeMap(node.getAttributes())));
    }

    @Override
    public XsdWhiteSpace clone(Map<String, String> placeHolderAttributes) {
        return new XsdWhiteSpace(this.value, this.fixed);
    }

    public String getValue() {
        return value;
    }

    public boolean isFixed() {
        return fixed;
    }
}
