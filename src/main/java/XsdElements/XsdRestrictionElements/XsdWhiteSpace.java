package XsdElements.XsdRestrictionElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.HashMap;

public class XsdWhiteSpace extends XsdAbstractRestrictionChild {

    public static String XSD_TAG = "xsd:whiteSpace";
    public static String XS_TAG = "xs:whiteSpace";

    private String value;

    public XsdWhiteSpace(String value){
        this.value = value;
    }

    private XsdWhiteSpace(HashMap<String, String> elementFieldsMap) {
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
        return ReferenceBase.createFromXsd(new XsdWhiteSpace(convertNodeMap(node.getAttributes())));
    }

    @Override
    public XsdWhiteSpace clone(HashMap<String, String> placeHolderAttributes) {
        return new XsdWhiteSpace(this.value);
    }

    public String getValue() {
        return value;
    }
}
