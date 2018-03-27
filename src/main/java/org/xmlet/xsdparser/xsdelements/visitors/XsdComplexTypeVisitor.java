package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;

public class XsdComplexTypeVisitor extends AttributesVisitor {

    private final XsdComplexType owner;

    public XsdComplexTypeVisitor(XsdComplexType owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdComplexType getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdMultipleElements element) {
        super.visit(element);

        owner.setChildElement(ReferenceBase.createFromXsd(element));
    }

    @Override
    public void visit(XsdGroup element) {
        super.visit(element);

        owner.setChildElement(ReferenceBase.createFromXsd(element));
    }

    @Override
    public void visit(XsdComplexContent element) {
        super.visit(element);

        owner.setComplexContent(element);
    }

    @Override
    public void visit(XsdSimpleContent element) {
        super.visit(element);

        owner.setSimpleContent(element);
    }
}