package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XsdExtension extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:extension";
    public static final String XS_TAG = "xs:extension";

    private XsdElementVisitor xsdElementVisitor = new ExtensionXsdElementVisitor();

    private ReferenceBase childElement;
    private List<ReferenceBase> attributeGroups = new ArrayList<>();
    private List<ReferenceBase> attributes = new ArrayList<>();

    private String base;

    private XsdExtension(XsdAbstractElement parent, Map<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdExtension(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.base = elementFieldsMap.getOrDefault(BASE, base);
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
    public XsdExtension clone(Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdExtension elementCopy = new XsdExtension(this.getParent(), placeHolderAttributes);

        elementCopy.childElement = this.childElement;
        elementCopy.attributeGroups.addAll(this.attributeGroups);
        elementCopy.attributes.addAll(this.attributes);
        elementCopy.base = this.base;

        return elementCopy;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return childElement == null ? null : childElement.getElement().getElements();
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdExtension(convertNodeMap(node.getAttributes())));
    }

    class ExtensionXsdElementVisitor extends AnnotatedXsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdExtension.this;
        }

        @Override
        public void visit(XsdMultipleElements element) {
            super.visit(element);
            XsdExtension.this.childElement = ReferenceBase.createFromXsd(element);
        }

        @Override
        public void visit(XsdGroup element) {
            super.visit(element);
            XsdExtension.this.childElement = ReferenceBase.createFromXsd(element);
        }

        @Override
        public void visit(XsdAttribute attribute) {
            super.visit(attribute);
            XsdExtension.this.attributes.add(ReferenceBase.createFromXsd(attribute));
        }

        //TODO AttributeGroup
    }

}
