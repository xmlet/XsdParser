package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdExtension;
import org.xmlet.xsdparser.xsdelements.XsdRestriction;
import org.xmlet.xsdparser.xsdelements.XsdSimpleContent;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;

public class XsdSimpleContentVisitor extends XsdAnnotatedElementsVisitor {

    private final XsdSimpleContent owner;

    public XsdSimpleContentVisitor(XsdSimpleContent owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdSimpleContent getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdRestriction element) {
        super.visit(element);

        owner.setRestriction(ReferenceBase.createFromXsd(element));
    }

    @Override
    public void visit(XsdExtension element) {
        super.visit(element);

        owner.setExtension(ReferenceBase.createFromXsd(element));
    }
}