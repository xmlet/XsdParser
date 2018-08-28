package org.xmlet.xsdparser.xsdelements.enums;

import java.util.Arrays;
import java.util.List;

public enum WhiteSpaceEnum implements XsdEnum<WhiteSpaceEnum> {

    PRESERVE ("preserve"),
    COLLAPSE ("collapse"),
    REPLACE ("replace");

    private final String value;

    WhiteSpaceEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getVariableName() {
        return "whitespace";
    }

    @Override
    public WhiteSpaceEnum[] getValues() {
        return WhiteSpaceEnum.values();
    }

    @Override
    public List<String> getSupportedValues() {
        return Arrays.asList(PRESERVE.getValue(),
                            COLLAPSE.getValue(),
                            REPLACE.getValue());
    }
}
