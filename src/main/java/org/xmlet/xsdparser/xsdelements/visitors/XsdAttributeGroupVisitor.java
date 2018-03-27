package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAttribute;
import org.xmlet.xsdparser.xsdelements.XsdAttributeGroup;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;

public class XsdAttributeGroupVisitor extends XsdAnnotatedElementsVisitor {

    private final XsdAttributeGroup owner;

    public XsdAttributeGroupVisitor(XsdAttributeGroup owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdAttributeGroup getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdAttribute element) {
        super.visit(element);

        owner.addAttribute(ReferenceBase.createFromXsd(element));
    }
}