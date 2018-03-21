package org.xmlet.xsdparser.xsdelements.elementswrapper;

import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.XsdReferenceElement;

/**
 * UnsolvedReference is a wrapper class for all XsdReferenceElements which should be resolved
 * in the parsing process, if possible.
 */
public class UnsolvedReference extends ReferenceBase {

    private String ref;

    UnsolvedReference(XsdReferenceElement element){
        super(element);
        this.ref = getRef(element);
    }

    public UnsolvedReference(String refType, XsdReferenceElement element){
        super(element);
        this.ref = refType;
    }

    public String getRef() {
        return ref;
    }

    public XsdAbstractElement getParent() {
        return element.getParent();
    }

    @Override
    public XsdReferenceElement getElement() {
        return (XsdReferenceElement) element;
    }
}
