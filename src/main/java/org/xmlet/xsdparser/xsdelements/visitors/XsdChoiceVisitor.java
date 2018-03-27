package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdChoice;
import org.xmlet.xsdparser.xsdelements.XsdElement;
import org.xmlet.xsdparser.xsdelements.XsdGroup;
import org.xmlet.xsdparser.xsdelements.XsdSequence;

public class XsdChoiceVisitor extends XsdAnnotatedElementsVisitor {

    private final XsdChoice owner;

    public XsdChoiceVisitor(XsdChoice owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdChoice getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdElement element) {
        super.visit(element);
        owner.addElement(element);
    }

    @Override
    public void visit(XsdGroup element) {
        super.visit(element);
        owner.addElement(element);
    }

    @Override
    public void visit(XsdChoice element) {
        super.visit(element);
        owner.addElement(element);
    }

    @Override
    public void visit(XsdSequence element) {
        super.visit(element);
        owner.addElement(element);
    }
}