package org.xmlet.xsdparser.xsdelements.elementswrapper;

import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.XsdReferenceElement;

/**
 * UnsolvedReference is a wrapper class for all XsdReferenceElements which should be resolved
 * in the parsing process, if possible.
 */
public class UnsolvedReference extends ReferenceBase {

    private String ref;
    private final XsdReferenceElement element;

    UnsolvedReference(XsdReferenceElement element){
        this.ref = getRef(element);
        this.element = element;
    }

    public UnsolvedReference(String refType, XsdReferenceElement element){
        this.ref = refType;
        this.element = element;
    }

    public String getRef() {
        return ref;
    }

    public XsdAbstractElement getParent() {
        return element.getParent();
    }

    public XsdReferenceElement getElement() {
        return element;
    }
}
