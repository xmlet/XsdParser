package org.xmlet.xsdparser.xsdelements.elementswrapper;

import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.XsdNamedElements;

/**
 * UnsolvedReference is a wrapper class for all objects which should be resolved in the parsing process, if possible.
 */
public class UnsolvedReference extends ReferenceBase {

    private String ref;
    private boolean isTypeRef;

    UnsolvedReference(XsdNamedElements element){
        super(element);
        this.ref = getRef(element);
        this.isTypeRef = false;
    }

    public UnsolvedReference(String refType, XsdNamedElements element){
        super(element);
        this.ref = refType;
        this.isTypeRef = true;
    }

    public String getRef() {
        return ref;
    }

    public boolean isTypeRef() {
        return isTypeRef;
    }

    public XsdAbstractElement getParent() {
        return element.getParent();
    }

    @Override
    public XsdNamedElements getElement() {
        return (XsdNamedElements) element;
    }
}
