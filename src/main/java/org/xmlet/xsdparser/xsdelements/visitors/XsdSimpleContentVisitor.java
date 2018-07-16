package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAnnotation;
import org.xmlet.xsdparser.xsdelements.XsdExtension;
import org.xmlet.xsdparser.xsdelements.XsdRestriction;
import org.xmlet.xsdparser.xsdelements.XsdSimpleContent;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;

/**
 * Represents the restrictions of the {@link XsdSimpleContent} element, which can only contain {@link XsdRestriction} or
 * {@link XsdExtension} as children. Can also have {@link XsdAnnotation} as children as per inheritance of
 * {@link XsdAnnotatedElementsVisitor}.
 */
public class XsdSimpleContentVisitor extends XsdAnnotatedElementsVisitor {

    /**
     * The {@link XsdSimpleContent} instance which owns this {@link XsdSimpleContentVisitor} instance. This way this
     * visitor instance can perform changes in the {@link XsdSimpleContent} object.
     */
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