package org.xmlet.xsdparser.xsdelements.elementswrapper;

import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.XsdNamedElements;

/**
 * NamedConcreteElement is a wrapper class for an {@link XsdNamedElements} object which are {@link XsdAbstractElement}
 * objects which have a name attribute.
 */
public class NamedConcreteElement extends ConcreteElement{

    private String name;

    NamedConcreteElement(XsdNamedElements element, String name){
        super(element);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public XsdNamedElements getElement() {
        return (XsdNamedElements) element;
    }
}
