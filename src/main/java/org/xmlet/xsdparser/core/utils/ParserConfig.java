package org.xmlet.xsdparser.core.utils;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;

import java.util.Map;
import java.util.function.BiFunction;

public interface ParserConfig {

    Map<String, String> getXsdTypesToJava();

    Map<String, BiFunction<XsdParserCore, Node, ReferenceBase>> getParseMappers();

}
