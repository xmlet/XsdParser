package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

@SuppressWarnings("ALL")
public interface XsdElementVisitor {

    default void visit(XsdAll element) {
        visit((XsdMultipleElements) element);
    }

    default void visit(XsdAttribute element) {
        visit((XsdReferenceElement) element);
    }

    default void visit(XsdAttributeGroup element){
        visit((XsdReferenceElement) element);
    }

    default void visit(XsdChoice element) {
        visit((XsdMultipleElements) element);
    }

    default void visit(XsdComplexType element) {}

    default void visit(XsdElement element){
        visit((XsdReferenceElement) element);
    }

    default void visit(XsdGroup element){
        visit((XsdReferenceElement) element);
    }

    default void visit(XsdSequence element){
        visit((XsdMultipleElements) element);
    }

    default void visit(XsdMultipleElements element){}

    private void visit(XsdReferenceElement element){
        ReferenceBase referenceBase = ReferenceBase.createFromXsd(element);

        if (referenceBase instanceof UnsolvedReference){
            XsdParser.getInstance().addUnsolvedReference((UnsolvedReference) referenceBase);
        }
    }

    default void visit(XsdSimpleType element) {}

    default void visit(XsdRestriction element) {}

    default void visit(XsdList element) {}

    default void visit(XsdUnion element) {}

    default void visit(XsdEnumeration element) {}

    default void visit(XsdFractionDigits element) {}

    default void visit(XsdLength element) {}

    default void visit(XsdMaxExclusive element) {}

    default void visit(XsdMaxInclusive element) {}

    default void visit(XsdMaxLength element) {}

    default void visit(XsdMinExclusive element) {}

    default void visit(XsdMinInclusive element) {}

    default void visit(XsdMinLength element) {}

    default void visit(XsdPattern element) {}

    default void visit(XsdTotalDigits element) {}

    default void visit(XsdWhiteSpace element) {}

    default void visit(XsdExtension element) {}

    default void visit(XsdComplexContent element) {}

    default void visit(XsdSimpleContent element) {}

    default void visit(XsdDocumentation element) {}

    default void visit(XsdAppInfo element) {}

    default void visit(XsdAnnotation xsdAnnotation) {}

    XsdAbstractElement getOwner();

}
