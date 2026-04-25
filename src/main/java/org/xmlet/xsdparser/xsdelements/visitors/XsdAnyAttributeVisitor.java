package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAnnotation;
import org.xmlet.xsdparser.xsdelements.XsdAnyAttribute;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

/**
 * Represents the children of {@code xs:anyAttribute}: the empty content model plus the
 * inherited {@code xs:annotation} from {@link XsdAnnotatedElementsVisitor}, restricted
 * to at most one annotation per the schema-for-schemas content model.
 */
public class XsdAnyAttributeVisitor extends XsdAnnotatedElementsVisitor {

    private boolean annotationSeen = false;

    public XsdAnyAttributeVisitor(XsdAnyAttribute owner) {
        super(owner);
    }

    @Override
    public void visit(XsdAnnotation element) {
        if (annotationSeen) {
            throw new ParsingException(XsdAnyAttribute.XSD_TAG + " element: at most one "
                    + XsdAnnotation.XSD_TAG + " is allowed.");
        }
        annotationSeen = true;
        super.visit(element);
    }
}
