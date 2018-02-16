package XsdElements.XsdRestrictionElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import XsdElements.Visitors.VisitorNotFoundException;
import XsdElements.XsdAbstractElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class XsdAbstractRestrictionChild extends XsdAbstractElement {
    @Override
    public Visitor getVisitor() {
        throw new VisitorNotFoundException("XsdRestrictionChildren should not have visits.");
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return new ArrayList<>();
    }
}
