package org.xmlet.xsdparser.core;

import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;

import java.util.ArrayList;
import java.util.List;

public class UnsolvedReferenceItem {

    private UnsolvedReference unsolvedReference;
    private List<XsdAbstractElement> parents;

    UnsolvedReferenceItem(UnsolvedReference unsolvedReference){
        this.unsolvedReference = unsolvedReference;
        this.parents = new ArrayList<>();

        this.parents.add(unsolvedReference.getParent());
    }

    UnsolvedReference getUnsolvedReference() {
        return unsolvedReference;
    }

    public List<XsdAbstractElement> getParents() {
        return parents;
    }
}
