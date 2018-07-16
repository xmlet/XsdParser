package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAnnotation;
import org.xmlet.xsdparser.xsdelements.XsdAttribute;
import org.xmlet.xsdparser.xsdelements.XsdSimpleType;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;

/**
 * Represents the restrictions of the {@link XsdAttribute} element, which can only contain {@link XsdSimpleType} elements
 * as children. Can also have {@link XsdAnnotation} children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
 */
public class XsdAttributeVisitor extends XsdAnnotatedElementsVisitor {

    /**
     * The {@link XsdAttribute} instance which owns this {@link XsdAttributeVisitor} instance. This way this visitor
     * instance can perform changes in the {@link XsdAttribute} object.
     */
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