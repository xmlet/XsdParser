package org.xmlet.xsdparser.xsdelements.enums;

import java.util.Arrays;
import java.util.List;

public enum FinalEnum implements XsdEnum<FinalEnum> {

    EXTENSION ("extension"),
    RESTRICTION ("restriction"),
    ALL ("#all");

    private final String value;

    FinalEnum(String value) {
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
    public FinalEnum[] getValues() {
        return FinalEnum.values();
    }

    @Override
    public List<String> getSupportedValues() {
        return Arrays.asList(EXTENSION.getValue(),
                             RESTRICTION.getValue(),
                             ALL.getValue());
    }
}
