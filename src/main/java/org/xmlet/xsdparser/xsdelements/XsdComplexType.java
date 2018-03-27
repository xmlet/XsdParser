package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdComplexTypeVisitor;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class XsdComplexType extends XsdReferenceElement {

    public static final String XSD_TAG = "xsd:complexType";
    public static final String XS_TAG = "xs:complexType";

    private XsdComplexTypeVisitor visitor = new XsdComplexTypeVisitor(this);

    private ReferenceBase childElement;

    private boolean elementAbstract;
    private boolean mixed;
    private String block;
    private String elementFinal;

    private XsdComplexContent complexContent;
    private XsdSimpleContent simpleContent;

    XsdComplexType(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.elementAbstract = Boolean.parseBoolean(elementFieldsMap.getOrDefault(ABSTRACT_TAG, "false"));
        this.mixed = Boolean.parseBoolean(elementFieldsMap.getOrDefault(MIXED_TAG, "false"));
        this.block = elementFieldsMap.getOrDefault(BLOCK_TAG, block);
        this.elementFinal = elementFieldsMap.getOrDefault(FINAL_TAG, elementFinal);
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public XsdComplexTypeVisitor getVisitor() {
        return visitor;
    }

    @Override
    public List<ReferenceBase> getElements() {
        return childElement == null ? null : childElement.getElement().getElements();
    }

    @Override
    public XsdComplexType clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(elementFieldsMap);
        placeHolderAttributes.remove(REF_TAG);

        XsdComplexType elementCopy = new XsdComplexType(placeHolderAttributes);
        elementCopy.setParent(this.parent);

        elementCopy.childElement = this.childElement;
        elementCopy.visitor.setAttributes(this.visitor.getAttributes());
        elementCopy.visitor.setAttributeGroups(this.visitor.getAttributeGroups());

        elementCopy.complexContent = this.complexContent;
        elementCopy.simpleContent = this.simpleContent;

        return elementCopy;
    }

    @Override
    public void replaceUnsolvedElements(NamedConcreteElement element) {
        super.replaceUnsolvedElements(element);

        visitor.replaceUnsolvedAttributes(element);
    }

    public XsdAbstractElement getXsdChildElement() {
        return childElement == null ? null : childElement.getElement();
    }

    public String getFinal() {
        return elementFinal;
    }

    List<ReferenceBase> getAttributes() {
        return visitor.getAttributes();
    }

    public Stream<XsdAttribute> getXsdAttributes() {
        return visitor.getXsdAttributes();
    }

    public Stream<XsdAttributeGroup> getXsdAttributeGroup() {
        return visitor.getXsdAttributeGroup();
    }

    public XsdSimpleContent getSimpleContent() {
        return simpleContent;
    }

    public XsdComplexContent getComplexContent() {
        return complexContent;
    }

    public boolean isMixed() {
        return mixed;
    }

    public boolean isElementAbstract() {
        return elementAbstract;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdComplexType(convertNodeMap(node.getAttributes())));
    }

    public void setChildElement(ReferenceBase childElement) {
        this.childElement = childElement;
    }

    public void setComplexContent(XsdComplexContent complexContent) {
        this.complexContent = complexContent;
    }

    public void setSimpleContent(XsdSimpleContent simpleContent) {
        this.simpleContent = simpleContent;
    }
}
