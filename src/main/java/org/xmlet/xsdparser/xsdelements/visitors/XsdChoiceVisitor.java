package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.*;

/**
 * Represents the restrictions of the {@link XsdChoice} element, which can contain {@link XsdElement}, {@link XsdChoice},
 * {@link XsdGroup} or {@link XsdSequence} as children. Can also have {@link XsdAnnotation} children as per inheritance
 * of {@link XsdAnnotatedElementsVisitor}.
 */
public class XsdChoiceVisitor extends XsdAnnotatedElementsVisitor {

    /**
     * The {@link XsdChoice} instance which owns this {@link XsdChoiceVisitor} instance. This way this visitor instance
     * can perform changes in the {@link XsdChoice} object.
     */
    private final XsdChoice owner;

    public XsdChoiceVisitor(XsdChoice owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdChoice getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdElement element) {
        super.visit(element);
        owner.addElement(element);
    }

    @Override
    public void visit(XsdGroup element) {
        super.visit(element);
        owner.addElement(element);
    }

    @Override
    public void visit(XsdChoice element) {
        super.visit(element);
        owner.addElement(element);
    }

    @Override
    public void visit(XsdSequence element) {
        super.visit(element);
        owner.addElement(element);
    }
}