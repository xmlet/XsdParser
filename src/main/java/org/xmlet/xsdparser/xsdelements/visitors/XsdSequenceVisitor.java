package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.*;

/**
 * Represents the restrictions of the {@link XsdSequence} element, which can contain {@link XsdElement}, {@link XsdGroup},
 * {@link XsdChoice} or {@link XsdSequence} as children. Can also have {@link XsdAnnotation} as children as per
 * inheritance of {@link XsdAnnotatedElementsVisitor}.
 */
public class XsdSequenceVisitor extends XsdAnnotatedElementsVisitor {

    /**
     * The {@link XsdSequence} instance which owns this {@link XsdSequenceVisitor} instance. This way this visitor instance
     * can perform changes in the {@link XsdSequence} object.
     */
    private final XsdSequence owner;

    public XsdSequenceVisitor(XsdSequence owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdSequence getOwner() {
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