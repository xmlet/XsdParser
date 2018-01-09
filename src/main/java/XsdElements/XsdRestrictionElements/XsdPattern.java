package XsdElements.XsdRestrictionElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.HashMap;

public class XsdPattern extends XsdAbstractRestrictionChild {

    public static String TAG = "xsd:pattern";

    private String value;

    public XsdPattern(String value){
        this.value = value;
    }

    XsdPattern(HashMap<String, String> elementFieldsMap) {
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
        return ReferenceBase.createFromXsd(new XsdPattern(convertNodeMap(node.getAttributes())));
    }

    public String getValue() {
        return value;
    }
}
