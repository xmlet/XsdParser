package XsdElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;

public class XsdGroup extends XsdReferenceElement {

    public static final String TAG = "xsd:group";

    private GroupVisitor visitor = new GroupVisitor();

    private XsdMultipleElements childElement;

    private XsdGroup(XsdAbstractElement parent, HashMap<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdGroup(HashMap<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        this.setParent(visitor.getOwner());
    }

    @Override
    public GroupVisitor getVisitor() {
        return visitor;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return childElement.getElements();
    }

    @Override
    public XsdAbstractElement createCopyWithAttributes(HashMap<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdGroup elementCopy = new XsdGroup(this.getParent(), placeHolderAttributes);

        elementCopy.setChildElement(this.getChildElement());

        return elementCopy;
    }

    private void setChildElement(XsdMultipleElements childElement) {
        childElement.getElements().forEach(childElementObj -> childElementObj.getElement().setParent(this));
        this.childElement = childElement;
    }

    XsdMultipleElements getChildElement() {
        return childElement;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdGroup(convertNodeMap(node.getAttributes())));
    }

    class GroupVisitor extends Visitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdGroup.this;
        }

        @Override
        public void visit(XsdMultipleElements element) {
            super.visit(element);

            XsdGroup.this.setChildElement(element);
        }
    }
}
