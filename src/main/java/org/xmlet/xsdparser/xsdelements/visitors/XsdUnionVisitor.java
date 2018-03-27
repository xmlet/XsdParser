package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdSimpleType;
import org.xmlet.xsdparser.xsdelements.XsdUnion;

public class XsdUnionVisitor extends XsdAnnotatedElementsVisitor {

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
