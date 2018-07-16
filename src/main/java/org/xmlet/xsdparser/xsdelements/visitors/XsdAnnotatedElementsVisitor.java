package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAnnotatedElements;
import org.xmlet.xsdparser.xsdelements.XsdAnnotation;

/**
 * Represents the restrictions of all the XSD elements that can have an {@link XsdAnnotation} as children.
 */
public class XsdAnnotatedElementsVisitor implements XsdAbstractElementVisitor {

    /**
     * The {@link XsdAnnotatedElements} instance which owns this {@link XsdAnnotatedElementsVisitor} instance. This way
     * this visitor instance can perform changes in the {@link XsdAnnotatedElements} objects.
     */
    private final XsdAnnotatedElements owner;

    public XsdAnnotatedElementsVisitor(XsdAnnotatedElements owner){
        this.owner = owner;
    }

    @Override
    public void visit(XsdAnnotation element) {
        XsdAbstractElementVisitor.super.visit(element);

        owner.setAnnotation(element);
    }

    @Override
    public XsdAnnotatedElements getOwner() {
        return owner;
    }
}
