package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAnnotation;
import org.xmlet.xsdparser.xsdelements.XsdAppInfo;
import org.xmlet.xsdparser.xsdelements.XsdDocumentation;

public class XsdAbstractAnnotationVisitor implements XsdAbstractElementVisitor {

    private final XsdAnnotation owner;

    public XsdAbstractAnnotationVisitor(XsdAnnotation owner){
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