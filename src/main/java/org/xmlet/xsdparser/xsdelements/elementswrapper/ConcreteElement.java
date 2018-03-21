package org.xmlet.xsdparser.xsdelements.elementswrapper;

import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;

/**
 * XsdElement is a wrapper class for XsdAbstractElement which is fully resolved
 */
public class ConcreteElement extends ReferenceBase {

    ConcreteElement(XsdAbstractElement element){
        super(element);
    }

}
