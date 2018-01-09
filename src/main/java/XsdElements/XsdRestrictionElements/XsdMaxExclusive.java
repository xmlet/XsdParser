package XsdElements.XsdRestrictionElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.HashMap;

public class XsdMaxExclusive extends XsdAbstractRestrictionChild{

    public static String TAG = "xsd:maxExclusive";

    private int value;

    public XsdMaxExclusive(int value){
        this.value = value;
    }

    XsdMaxExclusive(HashMap<String, String> elementFieldsMap) {
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
        return ReferenceBase.createFromXsd(new XsdMaxExclusive(convertNodeMap(node.getAttributes())));
    }

    public int getValue() {
        return value;
    }
}
