package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdList;
import org.xmlet.xsdparser.xsdelements.XsdSimpleType;

public class XsdListVisitor extends XsdAnnotatedElementsVisitor {

    private final XsdList owner;

    public XsdListVisitor(XsdList owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdList getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdSimpleType element) {
        super.visit(element);

        owner.setSimpleType(element);
    }
}
