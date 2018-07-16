package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.*;

/**
 * Represents the restrictions of the {@link XsdSimpleType} element, which can contain {@link XsdList}, {@link XsdUnion}
 * or {@link XsdRestriction} as children. Can also have {@link XsdAnnotation} as children as per inheritance of
 * {@link XsdAnnotatedElementsVisitor}.
 */

public class XsdSimpleTypeVisitor extends XsdAnnotatedElementsVisitor {

    /**
     * The {@link XsdSimpleType} instance which owns this {@link XsdSimpleTypeVisitor} instance. This way this visitor
     * instance can perform changes in the {@link XsdSimpleType} object.
     */
    private final XsdSimpleType owner;

    public XsdSimpleTypeVisitor(XsdSimpleType owner) {
        super(owner);

        this.owner = owner;
    }

    @Override
    public XsdSimpleType getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdList element) {
        super.visit(element);

        owner.setList(element);
    }

    @Override
    public void visit(XsdUnion element) {
        super.visit(element);

        owner.setUnion(element);
    }

    @Override
    public void visit(XsdRestriction element) {
        super.visit(element);

        owner.setRestriction(element);
    }
}