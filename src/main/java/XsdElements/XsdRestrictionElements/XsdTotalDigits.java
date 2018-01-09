package XsdElements.XsdRestrictionElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.HashMap;

public class XsdTotalDigits extends XsdAbstractRestrictionChild{

    public static String TAG = "xsd:totalDigits";

    private int value;

    public XsdTotalDigits(int value){
        this.value = value;
    }

    XsdTotalDigits(HashMap<String, String> elementFieldsMap) {
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
        return ReferenceBase.createFromXsd(new XsdTotalDigits(convertNodeMap(node.getAttributes())));
    }

    public int getValue() {
        return value;
    }

}
