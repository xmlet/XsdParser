package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAnnotation;
import org.xmlet.xsdparser.xsdelements.XsdSelector;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

/**
 * Represents the children of {@code xs:selector}: only {@code xs:annotation}, inherited via
 * {@link XsdAnnotatedElementsVisitor}, restricted to at most one annotation.
 */
public class XsdSelectorVisitor extends XsdAnnotatedElementsVisitor {

    private boolean annotationSeen = false;

    public XsdSelectorVisitor(XsdSelector owner) {
        super(owner);
    }

    @Override
    public void visit(XsdAnnotation element) {
        if (annotationSeen) {
            throw new ParsingException(XsdSelector.XSD_TAG + " element: at most one "
                    + XsdAnnotation.XSD_TAG + " is allowed.");
        }
        annotationSeen = true;
        super.visit(element);
    }
}
