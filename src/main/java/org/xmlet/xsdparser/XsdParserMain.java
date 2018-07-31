package org.xmlet.xsdparser;

import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.XsdElement;
import org.xmlet.xsdparser.xsdelements.XsdSchema;

import java.util.List;
import java.util.stream.Collectors;

public class XsdParserMain {

    public static void main(String[] args){
        XsdParser parser = new XsdParser(args[0]);
        XsdSchema schema = parser.getResultSchemas().findFirst().get();
        List<XsdElement> parseResult = parser.getParseResult().collect(Collectors.toList());
    }
}
