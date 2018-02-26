package org.xmlet.xsdparser.xsdelements.visitors;

public class VisitorNotFoundException extends RuntimeException{

    public VisitorNotFoundException(String message){
        super(message);
    }
}
