package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.Map;

public class XsdAll extends XsdMultipleElements {

    public static final String XSD_TAG = "xsd:all";
    public static final String XS_TAG = "xs:all";

    private final AllXsdElementVisitor visitor = new AllXsdElementVisitor();

    private XsdAll(XsdAbstractElement parent, Map<String, String> elementFieldsMap){
        super(parent, elementFieldsMap);
    }

    private XsdAll(Map<String, String> elementFieldsMap){
        super(elementFieldsMap);
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    @Override
    public XsdElementVisitor getXsdElementVisitor() {
        return visitor;
    }

    @Override
    public XsdAbstractElement clone(Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdAll elementCopy = new XsdAll(this.getParent(), placeHolderAttributes);

        elementCopy.addElements(this.getElements());

        return elementCopy;
    }

    public static ReferenceBase parse(Node node) {
        return xsdParseSkeleton(node, new XsdAll(convertNodeMap(node.getAttributes())));
    }

    class AllXsdElementVisitor extends AnnotatedXsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdAll.this;
        }

        @Override
        public void visit(XsdElement element) {
            super.visit(element);

            XsdAll.this.addElement(element);
        }
    }
}
