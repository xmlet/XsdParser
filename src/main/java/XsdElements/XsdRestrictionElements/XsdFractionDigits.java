package XsdElements.XsdRestrictionElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.HashMap;

public class XsdFractionDigits extends XsdAbstractRestrictionChild {

    public static String XSD_TAG = "xsd:fractionDigits";
    public static String XS_TAG = "xs:fractionDigits";

    private int value;

    public XsdFractionDigits(int value){
        this.value = value;
    }

    private XsdFractionDigits(HashMap<String, String> elementFieldsMap) {
        setFields(elementFieldsMap);
    }

    @Override
    public void setFields(HashMap<String, String> elementFieldsMap) {
        if (elementFieldsMap != null){
            value = Integer.parseInt(elementFieldsMap.getOrDefault(VALUE, "0"));
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        this.setParent(visitor.getOwner());
    }

    public static ReferenceBase parse(Node node){
        return ReferenceBase.createFromXsd(new XsdFractionDigits(convertNodeMap(node.getAttributes())));
    }

    @Override
    public XsdFractionDigits clone(HashMap<String, String> placeHolderAttributes) {
        return new XsdFractionDigits(this.value);
    }

    public int getValue() {
        return value;
    }
}
