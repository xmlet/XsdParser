package org.xmlet.xsdparser.core.utils;

import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instance of an {@link UnsolvedReference} that the {@link XsdParser} wasn't able to solve. It contains
 * the respective {@link UnsolvedReference} object and a list of elements that contained this {@link UnsolvedReference}
 * object. This allows the user to assert which references are missing and where are they used, which may be useful to
 * correct the problems in the XSD file.
 */
public class UnsolvedReferenceItem {

    /**
     * A {@link UnsolvedReference} object that wasn't solved in the parsing process. This happened because its referred
     * element isn't present in the files that were parsed.
     */
    private UnsolvedReference unsolvedReference;

    /**
     * A list of parents which indicate all the places where the {@link UnsolvedReference} object was used, which cause
     * every element present in this list to not be fully correct.
     */
    private List<XsdAbstractElement> parents;

    public UnsolvedReferenceItem(UnsolvedReference unsolvedReference){
        this.unsolvedReference = unsolvedReference;
        this.parents = new ArrayList<>();

        this.parents.add(unsolvedReference.getParent());
    }

    public UnsolvedReference getUnsolvedReference() {
        return unsolvedReference;
    }

    public List<XsdAbstractElement> getParents() {
        return parents;
    }
}
