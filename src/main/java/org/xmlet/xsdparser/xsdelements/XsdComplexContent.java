package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class XsdComplexContent extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:complexContent";
    public static final String XS_TAG = "xs:complexContent";

    private XsdElementVisitor xsdElementVisitor = new ComplexContentXsdElementVisitor();

    private ReferenceBase restriction;
    private ReferenceBase extension;

    private boolean mixed;

    private XsdComplexContent(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.mixed = Boolean.parseBoolean(elementFieldsMap.getOrDefault(MIXED_TAG, "false"));
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

    public boolean isMixed() {
        return mixed;
    }

    public XsdExtension getXsdExtension() {
        return extension instanceof ConcreteElement ? (XsdExtension) extension.getElement() : null;
    }

    public XsdRestriction getXsdRestriction(){
        return restriction instanceof ConcreteElement ? (XsdRestriction) restriction.getElement() : null;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdComplexContent(convertNodeMap(node.getAttributes())));
    }

    class ComplexContentXsdElementVisitor extends AnnotatedXsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdComplexContent.this;
        }

        @Override
        public void visit(XsdRestriction element) {
            super.visit(element);

            XsdComplexContent.this.restriction = ReferenceBase.createFromXsd(element);
        }

        @Override
        public void visit(XsdExtension element) {
            super.visit(element);

            XsdComplexContent.this.extension = ReferenceBase.createFromXsd(element);
        }
    }

}
