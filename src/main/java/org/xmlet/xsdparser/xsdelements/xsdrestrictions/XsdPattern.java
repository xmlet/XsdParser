package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.Map;

public class XsdPattern extends XsdAbstractRestrictionChild {

    public static String XSD_TAG = "xsd:pattern";
    public static String XS_TAG = "xs:pattern";

    private String value;

    private XsdPattern(String value){
        this.value = value;
    }

    private XsdPattern(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            value = elementFieldsMap.getOrDefault(VALUE, value);
        }
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    public static ReferenceBase parse(Node node){
        return ReferenceBase.createFromXsd(new XsdPattern(convertNodeMap(node.getAttributes())));
    }

    @Override
    public XsdPattern clone(Map<String, String> placeHolderAttributes) {
        return new XsdPattern(this.value);
    }

    public String getValue() {
        return value;
    }
}
