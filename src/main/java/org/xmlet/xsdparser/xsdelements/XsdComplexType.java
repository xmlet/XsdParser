package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class XsdComplexType extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:complexType";
    public static final String XS_TAG = "xs:complexType";

    private ComplexTypeXsdElementVisitor visitor = new ComplexTypeXsdElementVisitor();

    private ReferenceBase childElement;

    private String name;
    private boolean elementAbstract;
    private boolean mixed;
    private String block;
    private String elementFinal;
    private List<ReferenceBase> attributeGroups = new ArrayList<>();
    private List<ReferenceBase> attributes = new ArrayList<>();

    private XsdComplexContent complexContent;
    private XsdSimpleContent simpleContent;

    XsdComplexType(XsdAbstractElement parent, Map<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdComplexType(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            super.setFields(elementFieldsMap);

            this.name = elementFieldsMap.getOrDefault(NAME, name);
            this.elementAbstract = Boolean.parseBoolean(elementFieldsMap.getOrDefault(ABSTRACT, "false"));
            this.mixed = Boolean.parseBoolean(elementFieldsMap.getOrDefault(MIXED, "false"));
            this.block = elementFieldsMap.getOrDefault(BLOCK, block);
            this.elementFinal = elementFieldsMap.getOrDefault(FINAL, elementFinal);
        }
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    @Override
    public ComplexTypeXsdElementVisitor getXsdElementVisitor() {
        return visitor;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return childElement == null ? null : childElement.getElement().getElements();
    }

    @Override
    public XsdComplexType clone(Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdComplexType elementCopy = new XsdComplexType(this.getParent(), placeHolderAttributes);

        elementCopy.childElement = this.childElement;
        elementCopy.attributes.addAll(this.getAttributes());
        elementCopy.attributeGroups = this.getAttributeGroups();
        return elementCopy;
    }

    @Override
    public void replaceUnsolvedElements(ConcreteElement element) {
        super.replaceUnsolvedElements(element);

        if (element.getElement() instanceof XsdAttributeGroup){
            XsdAttributeGroup attributeGroup = (XsdAttributeGroup) element.getElement();

            this.attributeGroups.add(element);
            this.attributes.addAll(attributeGroup.getElements());
        }
    }

    public XsdAbstractElement getXsdChildElement() {
        return childElement == null ? null : childElement.getElement();
    }

    public String getName() {
        return name;
    }

    public String getFinal() {
        return elementFinal;
    }

    List<ReferenceBase> getAttributes() {
        return attributes;
    }

    public Stream<XsdAttribute> getXsdAttributes() {
        return attributes.stream()
                        .filter(attribute -> attribute instanceof ConcreteElement)
                        .filter(attribute -> attribute.getElement() instanceof  XsdAttribute)
                        .map(attribute -> (XsdAttribute)attribute.getElement());
    }

    public Stream<XsdAttributeGroup> getXsdAttributeGroup() {
        return attributeGroups.stream()
                .filter(attributeGroup -> attributeGroup instanceof ConcreteElement)
                .map(attributeGroup -> (XsdAttributeGroup) attributeGroup.getElement());
    }

    private List<ReferenceBase> getAttributeGroups() {
        return attributeGroups;
    }

    @SuppressWarnings("unused")
    public XsdSimpleContent getSimpleContent() {
        return simpleContent;
    }

    @SuppressWarnings("unused")
    public XsdComplexContent getComplexContent() {
        return complexContent;
    }

    @SuppressWarnings("unused")
    public boolean isMixed() {
        return mixed;
    }

    @SuppressWarnings("unused")
    public boolean isElementAbstract() {
        return elementAbstract;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdComplexType(convertNodeMap(node.getAttributes())));
    }

    class ComplexTypeXsdElementVisitor extends AnnotatedXsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdComplexType.this;
        }

        @Override
        public void visit(XsdMultipleElements element) {
            super.visit(element);

            XsdComplexType.this.childElement = ReferenceBase.createFromXsd(element);
        }

        @Override
        public void visit(XsdGroup element) {
            super.visit(element);

            XsdComplexType.this.childElement = ReferenceBase.createFromXsd(element);
        }

        @Override
        public void visit(XsdAttribute attribute) {
            super.visit(attribute);
            XsdComplexType.this.attributes.add(ReferenceBase.createFromXsd(attribute));
        }

        @Override
        public void visit(XsdComplexContent element) {
            super.visit(element);

            XsdComplexType.this.complexContent = element;
        }

        @Override
        public void visit(XsdSimpleContent element) {
            super.visit(element);

            XsdComplexType.this.simpleContent = element;
        }

        /*
        //TODO
        @Override
        public void visit(XsdAttributeGroup attributeGroup) {
            super.visit(attributeGroup);

            XsdComplexType.this.attributeGroups.add(attributeGroup);
            XsdComplexType.this.attributes.addAll(attributeGroup.getElements());
        }
        */
    }
}
