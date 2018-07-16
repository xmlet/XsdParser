package org.xmlet.xsdparser.xsdelements.visitors;

import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;

/**
 * Exception that is thrown every time that an {@link XsdAbstractElement#accept} method is accessed when the concrete
 * element shouldn't receive visits.
 */
public class VisitorNotFoundException extends RuntimeException{

    public VisitorNotFoundException(String message){
        super(message);
    }

}
