package XsdElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.ElementsWrapper.UnsolvedReference;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.HashMap;

public class XsdSequence extends XsdMultipleElements{

    public static final String TAG = "xsd:sequence";

    private SequenceVisitor visitor = new SequenceVisitor();

    private XsdSequence(XsdAbstractElement parent, HashMap<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdSequence(HashMap<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        this.setParent(visitor.getOwner());
    }

    @Override
    public SequenceVisitor getVisitor() {
        return visitor;
    }

    @Override
    public XsdAbstractElement createCopyWithAttributes(HashMap<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdSequence elementCopy = new XsdSequence(this.getParent(), placeHolderAttributes);

        elementCopy.addElements(this.getElements());

        return elementCopy;
    }

    private void addElement(XsdGroup groupElement){
        XsdMultipleElements groupChild = groupElement.getChildElement();

        if (groupChild != null){
            super.addElements(groupElement.getChildElement().getElements());
        } else {
            super.addElement(new UnsolvedReference(groupElement));
        }
    }

    private void addElement(XsdChoice choiceElement){
        super.addElements(choiceElement.getElements());
    }

    private void addElement(XsdSequence sequenceElement){
        super.addElements(sequenceElement.getElements());
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdSequence(convertNodeMap(node.getAttributes())));
    }

    class SequenceVisitor extends Visitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdSequence.this;
        }

        @Override
        public void visit(XsdElement element) {
            super.visit(element);
            XsdSequence.this.addElement(ReferenceBase.createFromXsd(element));
        }

        @Override
        public void visit(XsdGroup element) {
            super.visit(element);
            XsdSequence.this.addElement(element);
        }

        @Override
        public void visit(XsdChoice element) {
            super.visit(element);
            XsdSequence.this.addElement(element);
        }

        @Override
        public void visit(XsdSequence element) {
            super.visit(element);
            XsdSequence.this.addElement(element);
        }
    }
}
