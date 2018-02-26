package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class XsdSimpleContent extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:simpleContent";
    public static final String XS_TAG = "xs:simpleContent";

    private XsdElementVisitor xsdElementVisitor = new SimpleContentXsdElementVisitor();

    private ReferenceBase restriction;
    private ReferenceBase extension;

    private XsdSimpleContent(XsdAbstractElement parent, Map<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdSimpleContent(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public XsdElementVisitor getXsdElementVisitor() {
        return xsdElementVisitor;
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    @Override
    public XsdSimpleContent clone(Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdSimpleContent elementCopy = new XsdSimpleContent(this.getParent(), placeHolderAttributes);

        elementCopy.restriction = this.restriction;
        elementCopy.extension = this.extension;

        return elementCopy;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return Collections.emptyList();
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdSimpleContent(convertNodeMap(node.getAttributes())));
    }

    class SimpleContentXsdElementVisitor extends AnnotatedXsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdSimpleContent.this;
        }

        @Override
        public void visit(XsdRestriction element) {
            super.visit(element);

            XsdSimpleContent.this.restriction = ReferenceBase.createFromXsd(element);
        }

        @Override
        public void visit(XsdExtension element) {
            super.visit(element);

            XsdSimpleContent.this.extension = ReferenceBase.createFromXsd(element);
        }
    }
}
