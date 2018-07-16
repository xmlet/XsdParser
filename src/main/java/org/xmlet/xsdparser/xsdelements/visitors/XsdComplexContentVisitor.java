package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdComplexContent;
import org.xmlet.xsdparser.xsdelements.XsdExtension;
import org.xmlet.xsdparser.xsdelements.XsdRestriction;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;

/**
 * Represents the restrictions of the {@link XsdComplexContent} element, which can only contain {@link XsdRestriction}
 * and {@link XsdExtension} as children. Can also have xsd:annotation children as per inheritance of
 * {@link XsdAnnotatedElementsVisitor}.
 */
public class XsdComplexContentVisitor extends XsdAnnotatedElementsVisitor {

    /**
     * The {@link XsdComplexContent} instance which owns this {@link XsdComplexContentVisitor} instance. This way this
     * visitor instance can perform changes in the {@link XsdComplexContent} object.
     */
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