package XsdElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;

public class XsdList extends XsdAbstractElement {

    public static final String XSD_TAG = "xsd:list";
    public static final String XS_TAG = "xs:list";

    private ListVisitor visitor = new ListVisitor();

    private XsdSimpleType simpleType;

    private String itemType;

    private XsdList(XsdAbstractElement parent, HashMap<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    public XsdList(HashMap<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    private XsdList(XsdAbstractElement parent) {
        super(parent);
    }

    public void setFields(HashMap<String, String> elementFieldsMap){
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.itemType = elementFieldsMap.getOrDefault(ITEM_TYPE, itemType);
        }
    }

    public String getItemType() {
        return itemType;
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        this.setParent(visitor.getOwner());
    }

    @Override
    public XsdAbstractElement createCopyWithAttributes(HashMap<String, String> placeHolderAttributes) {
        return null;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return null;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdList(convertNodeMap(node.getAttributes())));
    }

    class ListVisitor extends Visitor{

        @Override
        public XsdAbstractElement getOwner() {
            return XsdList.this;
        }

        @Override
        public void visit(XsdSimpleType element) {
            super.visit(element);

            XsdList.this.simpleType = element;
        }
    }
}
