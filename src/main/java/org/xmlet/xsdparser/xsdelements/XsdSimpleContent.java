package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class XsdSimpleContent extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:simpleContent";
    public static final String XS_TAG = "xs:simpleContent";

    private XsdElementVisitor xsdElementVisitor = new SimpleContentXsdElementVisitor();

    private ReferenceBase restriction;
    private ReferenceBase extension;

    private XsdSimpleContent(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public XsdElementVisitor getVisitor() {
        return xsdElementVisitor;
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        super.accept(xsdElementVisitor);
        xsdElementVisitor.visit(this);
    }

    public XsdExtension getXsdExtension() {
        return extension instanceof ConcreteElement ? (XsdExtension) extension.getElement() : null;
    }

    public XsdRestriction getXsdRestriction(){
        return restriction instanceof ConcreteElement ? (XsdRestriction) restriction.getElement() : null;
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
