package XsdElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.HashMap;

public class XsdAll extends XsdMultipleElements {

    public static final String XSD_TAG = "xsd:all";
    public static final String XS_TAG = "xs:all";

    private final AllVisitor visitor = new AllVisitor();

    private XsdAll(XsdAbstractElement parent, HashMap<String, String> elementFieldsMap){
        super(parent, elementFieldsMap);
    }

    private XsdAll(HashMap<String, String> elementFieldsMap){
        super(elementFieldsMap);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        this.setParent(visitor.getOwner());
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }

    @Override
    public XsdAbstractElement createCopyWithAttributes(HashMap<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());

        XsdAll elementCopy = new XsdAll(this.getParent(), placeHolderAttributes);

        elementCopy.addElements(this.getElements());

        return elementCopy;
    }

    public static ReferenceBase parse(Node node) {
        return xsdParseSkeleton(node, new XsdAll(convertNodeMap(node.getAttributes())));
    }

    class AllVisitor extends Visitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdAll.this;
        }

        @Override
        public void visit(XsdElement element) {
            super.visit(element);
            XsdAll.this.addElement(ReferenceBase.createFromXsd(element));
        }
    }
}
