package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

/**
 * Represents the restrictions of the {@link XsdRestriction} element, which can contain the following children:
 *      * {@link XsdEnumeration}
 *      * {@link XsdFractionDigits}
 *      * {@link XsdLength}
 *      * {@link XsdMaxExclusive}
 *      * {@link XsdMaxInclusive}
 *      * {@link XsdMaxLength}
 *      * {@link XsdMinExclusive}
 *      * {@link XsdMinInclusive}
 *      * {@link XsdMinLength}
 *      * {@link XsdPattern}
 *      * {@link XsdTotalDigits}
 *      * {@link XsdWhiteSpace}
 *      * {@link XsdSimpleType}
 * Can also have {@link XsdAttribute} and {@link XsdAttributeGroup} children as per inheritance of {@link AttributesVisitor}.
 * Can also have {@link XsdAnnotation} children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
 */
public class XsdRestrictionsVisitor extends AttributesVisitor {

    /**
     * The {@link XsdRestriction} instance which owns this {@link XsdRestrictionsVisitor} instance. This way this
     * visitor instance can perform changes in the {@link XsdRestriction} object.
     */
    private final XsdRestriction owner;

    public XsdRestrictionsVisitor(XsdRestriction owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdRestriction getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdEnumeration element) {
        super.visit(element);

        owner.add(element);
    }

    @Override
    public void visit(XsdFractionDigits element) {
        super.visit(element);

        owner.setFractionDigits(element);
    }

    @Override
    public void visit(XsdLength element) {
        super.visit(element);

        owner.setLength(element);
    }

    @Override
    public void visit(XsdMaxExclusive element) {
        super.visit(element);

        owner.setMaxExclusive(element);
    }

    @Override
    public void visit(XsdMaxInclusive element) {
        super.visit(element);

        owner.setMaxInclusive(element);
    }

    @Override
    public void visit(XsdMaxLength element) {
        super.visit(element);

        owner.setMaxLength(element);
    }

    @Override
    public void visit(XsdMinExclusive element) {
        super.visit(element);

        owner.setMinExclusive(element);
    }

    @Override
    public void visit(XsdMinInclusive element) {
        super.visit(element);

        owner.setMinInclusive(element);
    }

    @Override
    public void visit(XsdMinLength element) {
        super.visit(element);

        owner.setMinLength(element);
    }

    @Override
    public void visit(XsdPattern element) {
        super.visit(element);

        owner.setPattern(element);
    }

    @Override
    public void visit(XsdTotalDigits element) {
        super.visit(element);

        owner.setTotalDigits(element);
    }

    @Override
    public void visit(XsdWhiteSpace element) {
        super.visit(element);

        owner.setWhiteSpace(element);
    }

    @Override
    public void visit(XsdSimpleType element) {
        super.visit(element);

        owner.setSimpleType(element);
    }
}