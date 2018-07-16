package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;

/**
 * Represents the restrictions of the {@link XsdExtension} element, which can contain the following children:
 *      * {@link XsdAll}, {@link XsdSequence} , {@link XsdChoice} (represented by {@link XsdMultipleElements});
 *      * {@link XsdGroup};
 * Can also have {@link XsdAttribute} and {@link XsdAttributeGroup} children as per inheritance of {@link AttributesVisitor}.
 * Can also have {@link XsdAnnotation} children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
 */
public class XsdExtensionVisitor extends AttributesVisitor {

    /**
     * The {@link XsdExtension} instance which owns this {@link XsdExtensionVisitor} instance. This way this visitor
     * instance can perform changes in the {@link XsdExtension} object.
     */
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
