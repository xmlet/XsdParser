package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdRestriction;
import org.xmlet.xsdparser.xsdelements.XsdSimpleType;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

public class XsdRestrictionsVisitor extends AttributesVisitor {

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