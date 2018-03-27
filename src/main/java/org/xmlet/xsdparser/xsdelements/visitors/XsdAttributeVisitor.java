package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAttribute;
import org.xmlet.xsdparser.xsdelements.XsdSimpleType;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;

public class XsdAttributeVisitor extends XsdAnnotatedElementsVisitor {

    private final XsdAttribute owner;

    public XsdAttributeVisitor(XsdAttribute owner){
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdAttribute getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdSimpleType element) {
        super.visit(element);

        owner.setSimpleType(ReferenceBase.createFromXsd(element));
    }
}