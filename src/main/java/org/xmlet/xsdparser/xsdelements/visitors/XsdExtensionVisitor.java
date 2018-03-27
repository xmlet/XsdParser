package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdExtension;
import org.xmlet.xsdparser.xsdelements.XsdGroup;
import org.xmlet.xsdparser.xsdelements.XsdMultipleElements;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;

public class XsdExtensionVisitor extends AttributesVisitor {

    private final XsdExtension owner;

    public XsdExtensionVisitor(XsdExtension owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdExtension getOwner() {
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
}
