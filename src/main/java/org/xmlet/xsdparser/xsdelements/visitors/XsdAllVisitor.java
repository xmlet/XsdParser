package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAll;
import org.xmlet.xsdparser.xsdelements.XsdElement;

public class XsdAllVisitor extends XsdAnnotatedElementsVisitor {

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