package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdComplexType;
import org.xmlet.xsdparser.xsdelements.XsdElement;
import org.xmlet.xsdparser.xsdelements.XsdSimpleType;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;

public class XsdElementVisitor extends XsdAnnotatedElementsVisitor {

    private final XsdElement owner;

    public XsdElementVisitor(XsdElement owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdElement getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdComplexType element) {
        super.visit(element);

        owner.setComplexType(ReferenceBase.createFromXsd(element));
    }

    @Override
    public void visit(XsdSimpleType element) {
        super.visit(element);

        owner.setSimpleType(ReferenceBase.createFromXsd(element));
    }
}