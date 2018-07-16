package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAll;
import org.xmlet.xsdparser.xsdelements.XsdAnnotation;
import org.xmlet.xsdparser.xsdelements.XsdElement;

/**
 * Represents the restrictions of the {@link XsdAll} element, which can only contain {@link XsdElement} as children.
 * Can also have {@link XsdAnnotation} children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
 */
public class XsdAllVisitor extends XsdAnnotatedElementsVisitor {

    /**
     * The {@link XsdAll} instance which owns this {@link XsdAllVisitor} instance. This way this visitor instance can
     * perform changes in the {@link XsdAll} object.
     */
    private final XsdAll owner;

    public XsdAllVisitor(XsdAll owner){
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdAll getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdElement element) {
        super.visit(element);

        owner.addElement(element);
    }
}