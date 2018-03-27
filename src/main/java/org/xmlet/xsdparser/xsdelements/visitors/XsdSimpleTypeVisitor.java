package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdList;
import org.xmlet.xsdparser.xsdelements.XsdRestriction;
import org.xmlet.xsdparser.xsdelements.XsdSimpleType;
import org.xmlet.xsdparser.xsdelements.XsdUnion;

public class XsdSimpleTypeVisitor extends XsdAnnotatedElementsVisitor {

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