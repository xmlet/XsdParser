package org.xmlet.xsdparser.core.utils;

import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import java.util.function.Function;

public class ConfigEntryData {

    public final Function<ParseData, ReferenceBase> parserFunction;
    public final Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction;

    public ConfigEntryData(Function<ParseData, ReferenceBase> parserFunction, Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction){
        this.parserFunction = parserFunction;
        this.visitorFunction = visitorFunction;
    }
}
