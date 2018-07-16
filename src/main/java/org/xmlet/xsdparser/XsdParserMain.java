package org.xmlet.xsdparser;

import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.XsdElement;

import java.util.List;
import java.util.stream.Collectors;

public class XsdParserMain {

    public static void main(String[] args){
        List<XsdElement> parseResult = new XsdParser(args[0]).getParseResult().collect(Collectors.toList());
    }
}
