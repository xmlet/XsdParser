package org.xmlet.xsdparser.xsdelements.enums;

import java.util.List;

public interface XsdEnum<T> {

    String getVariableName();

    List<String> getSupportedValues();

    T[] getValues();

    String getValue();

}
