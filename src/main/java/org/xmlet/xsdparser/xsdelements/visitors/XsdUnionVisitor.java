package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAnnotation;
import org.xmlet.xsdparser.xsdelements.XsdSimpleType;
import org.xmlet.xsdparser.xsdelements.XsdUnion;

/**
 * Represents the restrictions of the {@link XsdUnion} element, which can only contain {@link XsdSimpleType} as children.
 * Can also have {@link XsdAnnotation} as children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
 */
public class XsdUnionVisitor extends XsdAnnotatedElementsVisitor {

    /**
     * The {@link XsdUnion} instance which owns this {@link XsdUnionVisitor} instance. This way this visitor instance
     * can perform changes in the {@link XsdUnion} object.
     */
    private final XsdUnion owner;

    public XsdUnionVisitor(XsdUnion owner) {
        super(owner);

        this.owner = owner;
    }

    @Override
    public XsdUnion getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdSimpleType element) {
        super.visit(element);

        owner.add(element);
    }
}
