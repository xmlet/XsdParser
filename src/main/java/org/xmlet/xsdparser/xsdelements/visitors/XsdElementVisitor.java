package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;

public abstract class XsdElementVisitor {

    public void visit(XsdAll element) {
        visit((XsdMultipleElements) element);
    }

    public void visit(XsdAttribute element) {
        visit((XsdReferenceElement) element);
    }

    public void visit(XsdAttributeGroup element){
        visit((XsdReferenceElement) element);
    }

    public void visit(XsdChoice element) {
        visit((XsdMultipleElements) element);
    }

    public void visit(XsdComplexType element) {}

    public void visit(XsdElement element){
        visit((XsdReferenceElement) element);
    }

    public void visit(XsdGroup element){
        visit((XsdReferenceElement) element);
    }

    public void visit(XsdSequence element){
        visit((XsdMultipleElements) element);
    }

    public void visit(XsdMultipleElements element){}

    private void visit(XsdReferenceElement element){
        ReferenceBase referenceBase = ReferenceBase.createFromXsd(element);

        if (referenceBase instanceof UnsolvedReference){
            XsdParser.getInstance().addUnsolvedReference((UnsolvedReference) referenceBase);
        }
    }

    public void visit(XsdSimpleType element) {}

    public void visit(XsdRestriction element) {}

    public void visit(XsdList element) {}

    public void visit(XsdUnion element) {}

    public void visit(XsdEnumeration element) {}

    public void visit(XsdFractionDigits element) {}

    public void visit(XsdLength element) {}

    public void visit(XsdMaxExclusive element) {}

    public void visit(XsdMaxInclusive element) {}

    public void visit(XsdMaxLength element) {}

    public void visit(XsdMinExclusive element) {}

    public void visit(XsdMinInclusive element) {}

    public void visit(XsdMinLength element) {}

    public void visit(XsdPattern element) {}

    public void visit(XsdTotalDigits element) {}

    public void visit(XsdWhiteSpace element) {}

    public void visit(XsdExtension element) {}

    public void visit(XsdComplexContent element) {}

    public void visit(XsdSimpleContent element) {}

    public void visit(XsdDocumentation element) {}

    public void visit(XsdAppInfo element) {}

    public void visit(XsdAnnotation xsdAnnotation) {}

    public abstract XsdAbstractElement getOwner();

}
