package XsdElements.XsdRestrictionElements;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.HashMap;

public class XsdMaxLength extends XsdAbstractRestrictionChild{

    public static String XSD_TAG = "xsd:maxLength";
    public static String XS_TAG = "xs:maxLength";

    private int value;

    public XsdMaxLength(int value){
        this.value = value;
    }

    XsdMaxLength(HashMap<String, String> elementFieldsMap) {
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
        return ReferenceBase.createFromXsd(new XsdMaxLength(convertNodeMap(node.getAttributes())));
    }

    public int getValue() {
        return value;
    }
}
