package XsdElements.XsdRestrictionElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.HashMap;

public class XsdMinExclusive extends XsdAbstractRestrictionChild {

    public static String XSD_TAG = "xsd:minExclusive";
    public static String XS_TAG = "xs:minExclusive";

    private int value;

    public XsdMinExclusive(int value){
        this.value = value;
    }

    private XsdMinExclusive(HashMap<String, String> elementFieldsMap) {
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
        return ReferenceBase.createFromXsd(new XsdMinExclusive(convertNodeMap(node.getAttributes())));
    }

    @Override
    public XsdMinExclusive clone(HashMap<String, String> placeHolderAttributes) {
        return new XsdMinExclusive(this.value);
    }

    public int getValue() {
        return value;
    }
}
