package XsdElements.XsdRestrictionElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.HashMap;

public class XsdFractionDigits extends XsdAbstractRestrictionChild {

    public static String TAG = "xsd:fractionDigits";

    private int value;

    public XsdFractionDigits(int value){
        this.value = value;
    }

    XsdFractionDigits(HashMap<String, String> elementFieldsMap) {
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

    public int getValue() {
        return value;
    }
}
