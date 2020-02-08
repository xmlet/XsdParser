package org.xmlet.xsdparser.core.utils;

import java.util.Map;

public interface ParserConfig {

    Map<String, String> getXsdTypesToJava();

    Map<String, ConfigEntryData> getParseMappers();

}
