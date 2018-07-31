package org.xmlet.xsdparser.xsdelements.enums;

import java.util.Arrays;
import java.util.List;

public enum SimpleTypeFinalEnum implements XsdEnum<SimpleTypeFinalEnum> {

    LIST ("list"),
    UNION ("union"),
    RESTRICTION ("restriction"),
    ALL ("#all");

    private final String value;

    SimpleTypeFinalEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getVariableName() {
        return "final";
    }

    @Override
    public SimpleTypeFinalEnum[] getValues() {
        return SimpleTypeFinalEnum.values();
    }

    @Override
    public List<String> getSupportedValues() {
        return Arrays.asList(RESTRICTION.getValue(),
                             LIST.getValue(),
                             UNION.getValue(),
                             ALL.getValue());
    }
}
