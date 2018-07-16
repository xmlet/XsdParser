package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;

/**
 * Represents the restrictions of the {@link XsdComplexType} element, which can contain the following children:
 *      * {@link XsdAll}, {@link XsdSequence}, {@link XsdChoice} (represented by {@link XsdMultipleElements});
 *      * {@link XsdGroup};
 *      * {@link XsdComplexContent};
 *      * {@link XsdSimpleContent};
 * Can also have {@link XsdAttribute} and {@link XsdAttributeGroup} as children as per inheritance of {@link AttributesVisitor}.
 * Can also have {@link XsdAnnotation} children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
 */
public class XsdComplexTypeVisitor extends AttributesVisitor {

    /**
     * The {@link XsdComplexType} instance which owns this {@link XsdComplexTypeVisitor} instance. This way this visitor
     * instance can perform changes in the {@link XsdComplexType} object.
     */
    private final XsdComplexType owner;

    public XsdComplexTypeVisitor(XsdComplexType owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdComplexType getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdMultipleElements element) {
        super.visit(element);

        owner.setChildElement(ReferenceBase.createFromXsd(element));
    }

    @Override
    public void visit(XsdGroup element) {
        super.visit(element);

        owner.setChildElement(ReferenceBase.createFromXsd(element));
    }

    @Override
    public void visit(XsdComplexContent element) {
        super.visit(element);

        owner.setComplexContent(element);
    }

    @Override
    public void visit(XsdSimpleContent element) {
        super.visit(element);

        owner.setSimpleContent(element);
    }
}