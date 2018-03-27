package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdGroup;
import org.xmlet.xsdparser.xsdelements.XsdMultipleElements;

public class XsdGroupVisitor extends XsdAnnotatedElementsVisitor {

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