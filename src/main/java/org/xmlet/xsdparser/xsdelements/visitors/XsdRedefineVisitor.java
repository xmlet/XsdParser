package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.*;

/**
 * Visitor for {@link XsdRedefine} elements. Handles child elements (simpleType, complexType, group, attributeGroup)
 * that are redefinitions of components from the included schema.
 */
public class XsdRedefineVisitor extends AttributesVisitor {

    private XsdRedefine owner;

    public XsdRedefineVisitor(XsdRedefine owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdRedefine getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdAnnotation element) {
        super.visit(element);
        owner.add(element);
    }

    @Override
    public void visit(XsdSimpleType element) {
        super.visit(element);
        owner.add(element);
    }

    @Override
    public void visit(XsdComplexType element) {
        super.visit(element);
        owner.add(element);
    }

    @Override
    public void visit(XsdGroup element) {
        super.visit(element);
        owner.add(element);
    }

    @Override
    public void visit(XsdAttributeGroup element) {
        super.visit(element);
        owner.add(element);
    }
}
