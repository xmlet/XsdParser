package org.xmlet.xsdparser.xsdelements.elementswrapper;

import org.xmlet.xsdparser.xsdelements.XsdReferenceElement;

public class NamedConcreteElement extends ConcreteElement{

    private String name;

    NamedConcreteElement(XsdReferenceElement element, String name){
        super(element);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public XsdReferenceElement getElement() {
        return (XsdReferenceElement) element;
    }
}
