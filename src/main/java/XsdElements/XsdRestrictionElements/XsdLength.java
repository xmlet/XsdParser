package XsdElements.XsdRestrictionElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.HashMap;

public class XsdLength extends XsdAbstractRestrictionChild{

    public static String XSD_TAG = "xsd:length";
    public static String XS_TAG = "xs:length";

    private int value;

    public XsdLength(int value){
        this.value = value;
    }

    private XsdLength(HashMap<String, String> elementFieldsMap) {
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
        return ReferenceBase.createFromXsd(new XsdLength(convertNodeMap(node.getAttributes())));
    }

    @Override
    public XsdLength clone(HashMap<String, String> placeHolderAttributes) {
        return new XsdLength(this.value);
    }

    public int getValue() {
        return value;
    }
}
