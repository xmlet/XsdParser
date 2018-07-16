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

/**
 * Represents the restrictions of the all elements that can contain {@link XsdAttribute} and {@link XsdAttributeGroup}.
 * This visitor contains the {@link AttributesVisitor#attributes} and {@link AttributesVisitor#attributeGroups} that
 * belong to the owner and apart from receiving the attributes in the visit method this visitor also performs the
 * {@link AttributesVisitor#replaceUnsolvedAttributes} method, which is a method that was shared by all the types that
 * contained a list of {@link XsdAttribute} and {@link XsdAttributeGroup} objects.
 * Can also have xsd:annotation children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
 */
public abstract class AttributesVisitor extends XsdAnnotatedElementsVisitor {

    /**
     * The list of {@link XsdAttributeGroup} instances received by this visitor, wrapped in a {@link ReferenceBase} object.
     */
    private List<ReferenceBase> attributeGroups = new ArrayList<>();

    /**
     * The list of {@link XsdAttribute} instances received by this visitor, wrapped in a {@link ReferenceBase} object.
     */
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

    public void setAttributes(List<ReferenceBase> attributes) {
        this.attributes = attributes;
    }

    public void setAttributeGroups(List<ReferenceBase> attributeGroups) {
        this.attributeGroups = attributeGroups;
    }

    /**
     * @return All the wrapped {@link XsdAttribute} objects received by this visitor.
     */
    public List<ReferenceBase> getAttributes() {
        return attributes;
    }

    /**
     * @return All the wrapped {@link XsdAttributeGroup} objects received by this visitor.
     */
    public List<ReferenceBase> getAttributeGroups() {
        return attributeGroups;
    }

    /**
     * @return All the {@link XsdAttribute} objects that are fully resolved by this visitor. The {@link XsdAttribute}
     * objects wrapped in {@link UnsolvedReference} objects are not returned.
     */
    public Stream<XsdAttribute> getXsdAttributes() {
        return attributes.stream()
                .filter(attribute -> attribute instanceof ConcreteElement)
                .filter(attribute -> attribute.getElement() instanceof  XsdAttribute)
                .map(attribute -> (XsdAttribute)attribute.getElement());
    }

    /**
     * @return All the {@link XsdAttributeGroup} objects that are fully resolved by this visitor. The
     * {@link XsdAttributeGroup} objects wrapped in {@link UnsolvedReference} objects are not returned.
     */
    public Stream<XsdAttributeGroup> getXsdAttributeGroup() {
        return attributeGroups.stream()
                .filter(attributeGroup -> attributeGroup instanceof ConcreteElement)
                .map(attributeGroup -> (XsdAttributeGroup) attributeGroup.getElement());
    }

    /**
     * Tries to match the received {@link NamedConcreteElement} object, with any of the elements present either in
     * {@link AttributesVisitor#attributeGroups} or {@link AttributesVisitor#attributes}. If a match occurs this method
     * performs all the required actions to fully exchange the {@link UnsolvedReference} object with the element parameter.
     * @param element The resolved element that will be match with the contents of this visitor in order to assert if
     *                there is anything to replace.
     */
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