package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdComplexContent;
import org.xmlet.xsdparser.xsdelements.XsdExtension;
import org.xmlet.xsdparser.xsdelements.XsdRestriction;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;

public class XsdComplexContentVisitor extends XsdAnnotatedElementsVisitor {

    private final XsdComplexContent owner;

    public XsdComplexContentVisitor(XsdComplexContent owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdComplexContent getOwner() {
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