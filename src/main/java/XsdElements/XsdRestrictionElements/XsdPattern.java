package XsdElements.XsdRestrictionElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.HashMap;

public class XsdPattern extends XsdAbstractRestrictionChild {

    public static String XSD_TAG = "xsd:pattern";
    public static String XS_TAG = "xs:pattern";

    private String value;

    public XsdPattern(String value){
        this.value = value;
    }

    private XsdPattern(HashMap<String, String> elementFieldsMap) {
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

    @Override
    public XsdPattern clone(HashMap<String, String> placeHolderAttributes) {
        return new XsdPattern(this.value);
    }

    public String getValue() {
        return value;
    }
}
