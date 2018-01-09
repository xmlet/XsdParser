package XsdElements.Visitors;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.ElementsWrapper.UnsolvedReference;
import XsdElements.*;
import XsdElements.XsdRestrictionElements.*;
import XsdParser.XsdParser;

public abstract class Visitor {

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

    public void visit(XsdComplexType element) {

    }

    public void visit(XsdElement element){
        visit((XsdReferenceElement) element);
    }

    public void visit(XsdGroup element){
        visit((XsdReferenceElement) element);
    }

    public void visit(XsdSequence element){
        visit((XsdMultipleElements) element);
    }

    public void visit(XsdMultipleElements element){

    }

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

    public abstract XsdAbstractElement getOwner();

}
