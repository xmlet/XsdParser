package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAnnotation;
import org.xmlet.xsdparser.xsdelements.XsdList;
import org.xmlet.xsdparser.xsdelements.XsdSimpleType;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

/**
 * Represents the restrictions of the {@link XsdList} element, which can only contain {@link XsdSimpleType} as children.
 * Can also have {@link XsdAnnotation} children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
 */
public class XsdListVisitor extends XsdAnnotatedElementsVisitor {

    /**
     * The {@link XsdList} instance which owns this {@link XsdListVisitor} instance. This way this visitor instance can
     * perform changes in the {@link XsdList} object.
     */
    private final XsdList owner;

    public XsdListVisitor(XsdList owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdList getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdSimpleType element) {
        super.visit(element);

        if (owner.getItemType() != null){
            throw new ParsingException(XsdList.XSD_TAG + " element: The element cannot have both the itemType attribute and a " + XsdSimpleType.XSD_TAG + " element as content at the same time." );
        }

        owner.setSimpleType(element);
    }
}
