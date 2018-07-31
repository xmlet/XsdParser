package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.*;

public class XsdSchemaVisitor extends AttributesVisitor {

    private XsdSchema owner;

    public XsdSchemaVisitor(XsdSchema owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdSchema getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdInclude element) {
        super.visit(element);

        owner.add(element);
    }

    @Override
    public void visit(XsdImport element) {
        super.visit(element);

        owner.add(element);
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

    @Override
    public void visit(XsdElement element) {
        super.visit(element);

        owner.add(element);
    }

    @Override
    public void visit(XsdAttribute element) {
        super.visit(element);

        owner.add(element);
    }
}
