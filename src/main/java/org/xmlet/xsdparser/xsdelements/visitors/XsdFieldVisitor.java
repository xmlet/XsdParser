package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAnnotation;
import org.xmlet.xsdparser.xsdelements.XsdField;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

/**
 * Represents the children of {@code xs:field}: only {@code xs:annotation}, inherited via
 * {@link XsdAnnotatedElementsVisitor}, restricted to at most one annotation.
 */
public class XsdFieldVisitor extends XsdAnnotatedElementsVisitor {

    private boolean annotationSeen = false;

    public XsdFieldVisitor(XsdField owner) {
        super(owner);
    }

    @Override
    public void visit(XsdAnnotation element) {
        if (annotationSeen) {
            throw new ParsingException(XsdField.XSD_TAG + " element: at most one "
                    + XsdAnnotation.XSD_TAG + " is allowed.");
        }
        annotationSeen = true;
        super.visit(element);
    }
}
