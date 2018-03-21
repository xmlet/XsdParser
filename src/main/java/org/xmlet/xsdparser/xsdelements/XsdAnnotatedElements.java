package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class XsdAnnotatedElements extends XsdIdentifierElements {

    private XsdAnnotation annotation;

    protected XsdAnnotatedElements(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    protected void setAnnotation(XsdAnnotation annotation){
        this.annotation = annotation;
    }

    public XsdAnnotation getAnnotation() {
        return annotation;
    }

    protected abstract class AnnotatedXsdElementVisitor implements XsdElementVisitor {
        @Override
        public void visit(XsdAnnotation element) {
            XsdElementVisitor.super.visit(element);

            setAnnotation(element);
        }
    }

    protected abstract class AttributesVisitor extends AnnotatedXsdElementVisitor{
        List<ReferenceBase> attributeGroups = new ArrayList<>();
        List<ReferenceBase> attributes = new ArrayList<>();

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

        Stream<XsdAttribute> getXsdAttributes() {
            return attributes.stream()
                    .filter(attribute -> attribute instanceof ConcreteElement)
                    .filter(attribute -> attribute.getElement() instanceof  XsdAttribute)
                    .map(attribute -> (XsdAttribute)attribute.getElement());
        }

        Stream<XsdAttributeGroup> getXsdAttributeGroup() {
            return attributeGroups.stream()
                    .filter(attributeGroup -> attributeGroup instanceof ConcreteElement)
                    .map(attributeGroup -> (XsdAttributeGroup) attributeGroup.getElement());
        }

        void replaceUnsolvedAttributes(NamedConcreteElement element){
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
}
