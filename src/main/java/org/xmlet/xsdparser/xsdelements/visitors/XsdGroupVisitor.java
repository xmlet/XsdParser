package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.*;

/**
 * Represents the restrictions of the {@link XsdGroup} element, which can contain {@link XsdAll}, {@link XsdSequence},
 * {@link XsdChoice} (represented by {@link XsdMultipleElements}) as children. Can also have {@link XsdAnnotation} as
 * children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
 */
public class XsdGroupVisitor extends XsdAnnotatedElementsVisitor {

    /**
     * The {@link XsdGroup} instance which owns this {@link XsdGroupVisitor} instance. This way this visitor instance
     * can perform changes in the {@link XsdGroup} object.
     */
    private final XsdGroup owner;

    public XsdGroupVisitor(XsdGroup owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdGroup getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdMultipleElements element) {
        super.visit(element);

        owner.setChildElement(element);
    }
}