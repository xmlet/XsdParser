package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class XsdComplexContent extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:complexContent";
    public static final String XS_TAG = "xs:complexContet";

    private XsdElementVisitor xsdElementVisitor = new ComplexContentXsdElementVisitor();

    private ReferenceBase restriction;
    private ReferenceBase extension;

    private boolean mixed;

    private XsdComplexContent(XsdAbstractElement parent, Map<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdComplexContent(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.mixed = Boolean.parseBoolean(elementFieldsMap.getOrDefault(MIXED, "false"));
        }
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
    public XsdComplexContent clone(Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdComplexContent elementCopy = new XsdComplexContent(this.getParent(), placeHolderAttributes);

        elementCopy.restriction = this.restriction;
        elementCopy.extension = this.extension;

        return elementCopy;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return Collections.emptyList();
    }

    @SuppressWarnings("unused")
    public boolean isMixed() {
        return mixed;
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
