package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.Map;

public class XsdChoice extends XsdMultipleElements{

    public static final String XSD_TAG = "xsd:choice";
    public static final String XS_TAG = "xs:choice";

    private ChoiceXsdElementVisitor visitor = new ChoiceXsdElementVisitor();

    private XsdChoice(XsdAbstractElement parent, Map<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdChoice(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    @Override
    public ChoiceXsdElementVisitor getXsdElementVisitor() {
        return visitor;
    }

    @Override
    public XsdAbstractElement clone(Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdChoice elementCopy = new XsdChoice(this.getParent(), placeHolderAttributes);

        elementCopy.addElements(this.getElements());

        return elementCopy;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdChoice(convertNodeMap(node.getAttributes())));
    }

    class ChoiceXsdElementVisitor extends AnnotatedXsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdChoice.this;
        }

        @Override
        public void visit(XsdElement element) {
            super.visit(element);
            XsdChoice.this.addElement(element);
        }

        @Override
        public void visit(XsdGroup element) {
            super.visit(element);
            XsdChoice.this.addElement(element);
        }

        @Override
        public void visit(XsdChoice element) {
            super.visit(element);
            XsdChoice.this.addElement(element);
        }

        @Override
        public void visit(XsdSequence element) {
            super.visit(element);
            XsdChoice.this.addElement(element);
        }
    }

}
