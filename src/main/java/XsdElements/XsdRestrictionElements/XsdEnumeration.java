package XsdElements.XsdRestrictionElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.HashMap;

public class XsdEnumeration extends XsdAbstractRestrictionChild{

    public static String TAG = "xsd:enumeration";

    private String value;

    public XsdEnumeration(String value){
        this.value = value;
    }

    XsdEnumeration(HashMap<String, String> elementFieldsMap) {
        setFields(elementFieldsMap);
    }

    @Override
    public void setFields(HashMap<String, String> elementFieldsMap) {
        if (elementFieldsMap != null){
            value = elementFieldsMap.getOrDefault(VALUE, value);
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        this.setParent(visitor.getOwner());
    }

    public static ReferenceBase parse(Node node){
        return ReferenceBase.createFromXsd(new XsdEnumeration(convertNodeMap(node.getAttributes())));
    }

    public String getValue() {
        return value;
    }

}
