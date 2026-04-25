package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAnnotation;
import org.xmlet.xsdparser.xsdelements.XsdField;
import org.xmlet.xsdparser.xsdelements.XsdIdentityConstraint;
import org.xmlet.xsdparser.xsdelements.XsdSelector;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

/**
 * Children visitor shared by {@link org.xmlet.xsdparser.xsdelements.XsdUnique},
 * {@link org.xmlet.xsdparser.xsdelements.XsdKey}, and
 * {@link org.xmlet.xsdparser.xsdelements.XsdKeyref}.
 *
 * <p>Per W3C XSD 1.0 Part 1 §3.11.2 ({@code keybase} type), the content model is exactly:
 * {@code (annotation?, (selector, field+))} — i.e. an optional {@code xs:annotation},
 * followed by exactly one {@code xs:selector}, followed by one or more {@code xs:field}
 * elements, in that order. This visitor enforces both ordering and the cardinality of
 * {@code xs:selector}; the {@code field+} minimum is checked in
 * {@link XsdIdentityConstraint#validateSchemaRules()}.
 */
public class XsdIdentityConstraintVisitor extends XsdAnnotatedElementsVisitor {

    private final XsdIdentityConstraint owner;
    private boolean annotationSeen = false;
    private boolean selectorSeen = false;
    private boolean fieldSeen = false;

    public XsdIdentityConstraintVisitor(XsdIdentityConstraint owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public XsdIdentityConstraint getOwner() {
        return owner;
    }

    @Override
    public void visit(XsdAnnotation element) {
        if (annotationSeen) {
            throw new ParsingException("Identity constraint: at most one " + XsdAnnotation.XSD_TAG + " is allowed.");
        }
        if (selectorSeen || fieldSeen) {
            throw new ParsingException(XsdAnnotation.XSD_TAG + " element must precede "
                    + XsdSelector.XSD_TAG + " and " + XsdField.XSD_TAG + " in an identity constraint.");
        }
        annotationSeen = true;
        super.visit(element);
    }

    @Override
    public void visit(XsdSelector element) {
        if (selectorSeen) {
            throw new ParsingException(XsdSelector.XSD_TAG + " element: at most one is allowed inside an identity constraint.");
        }
        if (fieldSeen) {
            throw new ParsingException(XsdSelector.XSD_TAG + " element must precede " + XsdField.XSD_TAG + ".");
        }
        super.visit(element);
        owner.setSelector(element);
        selectorSeen = true;
    }

    @Override
    public void visit(XsdField element) {
        if (!selectorSeen) {
            throw new ParsingException(XsdField.XSD_TAG + " element must follow " + XsdSelector.XSD_TAG + ".");
        }
        super.visit(element);
        owner.addField(element);
        fieldSeen = true;
    }
}
