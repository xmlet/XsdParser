package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAnnotatedElements;
import org.xmlet.xsdparser.xsdelements.XsdAttribute;
import org.xmlet.xsdparser.xsdelements.XsdAttributeGroup;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class AttributesVisitor extends XsdAnnotatedElementsVisitor {
    private List<ReferenceBase> attributeGroups = new ArrayList<>();
    private List<ReferenceBase> attributes = new ArrayList<>();

    AttributesVisitor(XsdAnnotatedElements owner){
        super(owner);
    }

    @Override
    public void visit(XsdAttribute attribute) {
        super.visit(attribute);

        attributes.add(ReferenceBase.createFromXsd(attribute));
    }

    @Override
    public void visit(XsdAttributeGroup attributeGroup) {
        super.visit(attributeGroup);

        attributeGroups.add(ReferenceBase.createFromXsd(attributeGroup));
    }

    public List<ReferenceBase> getAttributes() {
        return attributes;
    }

    public List<ReferenceBase> getAttributeGroups() {
        return attributeGroups;
    }

    public void setAttributes(List<ReferenceBase> attributes) {
        this.attributes = attributes;
    }

    public void setAttributeGroups(List<ReferenceBase> attributeGroups) {
        this.attributeGroups = attributeGroups;
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

    public void replaceUnsolvedAttributes(NamedConcreteElement element){
        if (element.getElement() instanceof XsdAttributeGroup){
            attributeGroups.stream()
                    .filter(attributeGroup -> attributeGroup instanceof UnsolvedReference && ((UnsolvedReference) attributeGroup).getRef().equals(element.getName()))
                    .findFirst().ifPresent(referenceBase -> {
                attributeGroups.remove(referenceBase);
                attributeGroups.add(element);
                attributes.addAll(element.getElement().getElements());

                element.getElement().setParent(getOwner());
            });
        }

        if (element.getElement() instanceof XsdAttribute ){
            attributes.stream()
                    .filter(attribute -> attribute instanceof UnsolvedReference && ((UnsolvedReference) attribute).getRef().equals(element.getName()))
                    .findFirst().ifPresent(referenceBase -> {
                attributes.remove(referenceBase);
                attributes.add(element);
                element.getElement().setParent(getOwner());
            });
        }
    }
}