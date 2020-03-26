package org.xmlet.xsdparser.core.utils;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import java.util.function.Function;

public class ParseData {

    public final XsdParserCore parserInstance;
    public final Node node;
    public final Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction;

    public ParseData(XsdParserCore parserInstance, Node node, Function<XsdAbstractElement, XsdAbstractElementVisitor> visitor){
        this.parserInstance = parserInstance;
        this.node = node;
        this.visitorFunction = visitor;
    }
}
