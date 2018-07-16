package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAnnotation;
import org.xmlet.xsdparser.xsdelements.XsdAppInfo;
import org.xmlet.xsdparser.xsdelements.XsdDocumentation;

/**
 * Represents the restrictions of the {@link XsdAnnotation} element, which can only contain {@link XsdAppInfo} and
 * {@link XsdDocumentation} as children.
 */
public class XsdAnnotationVisitor implements XsdAbstractElementVisitor {

    /**
     * The {@link XsdAnnotation} instance which owns this {@link XsdAnnotationVisitor} instance. This way this visitor
     * instance can perform changes in the {@link XsdAnnotation} object.
     */
    private final XsdAnnotation owner;

    public XsdAnnotationVisitor(XsdAnnotation owner){
        this.owner = owner;
    }

    public XsdAnnotation getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdAppInfo element) {
        XsdAbstractElementVisitor.super.visit(element);

        owner.add(element);
    }

    @Override
    public void visit(XsdDocumentation element) {
        XsdAbstractElementVisitor.super.visit(element);

        owner.add(element);
    }

}