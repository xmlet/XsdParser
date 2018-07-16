package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAnnotation;
import org.xmlet.xsdparser.xsdelements.XsdComplexType;
import org.xmlet.xsdparser.xsdelements.XsdElement;
import org.xmlet.xsdparser.xsdelements.XsdSimpleType;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;

/**
 * Represents the restrictions of the {@link XsdElement} element, which can only contain {@link XsdSimpleType} or
 * {@link XsdComplexType} as children. Can also have {@link XsdAnnotation} children as per inheritance of
 * {@link XsdAnnotatedElementsVisitor}.
 */
public class XsdElementVisitor extends XsdAnnotatedElementsVisitor {

    /**
     * The {@link XsdElement} instance which owns this {@link XsdElementVisitor} instance. This way this visitor instance
     * can perform changes in the {@link XsdElement} object.
     */
    private final XsdElement owner;

    public XsdElementVisitor(XsdElement owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdElement getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdComplexType element) {
        super.visit(element);

        owner.setComplexType(ReferenceBase.createFromXsd(element));
    }

    @Override
    public void visit(XsdSimpleType element) {
        super.visit(element);

        owner.setSimpleType(ReferenceBase.createFromXsd(element));
    }
}