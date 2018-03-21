package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class XsdAll extends XsdMultipleElements {

    public static final String XSD_TAG = "xsd:all";
    public static final String XS_TAG = "xs:all";

    private final AllXsdElementVisitor visitor = new AllXsdElementVisitor();

    private XsdAll(@NotNull Map<String, String> elementFieldsMapParam){
        super(elementFieldsMapParam);
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        super.accept(xsdElementVisitor);
        xsdElementVisitor.visit(this);
    }

    @Override
    public XsdElementVisitor getVisitor() {
        return visitor;
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
