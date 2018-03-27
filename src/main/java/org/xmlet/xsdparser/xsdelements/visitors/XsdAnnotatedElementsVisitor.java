package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAnnotatedElements;
import org.xmlet.xsdparser.xsdelements.XsdAnnotation;

public class XsdAnnotatedElementsVisitor implements XsdAbstractElementVisitor {

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
