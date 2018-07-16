package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAnnotation;
import org.xmlet.xsdparser.xsdelements.XsdAttribute;
import org.xmlet.xsdparser.xsdelements.XsdAttributeGroup;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;

/**
 * Represents the restrictions of the {@link XsdAttributeGroup} element, which can only contain {@link XsdAttribute}
 * elements as children. Can also have {@link XsdAnnotation} children as per inheritance of
 * {@link XsdAnnotatedElementsVisitor}.
 */
public class XsdAttributeGroupVisitor extends XsdAnnotatedElementsVisitor {

    /**
     * The {@link XsdAttributeGroup} instance which owns this {@link XsdAttributeGroupVisitor} instance. This way this
     * visitor instance can perform changes in the {@link XsdAttributeGroup} object.
     */
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