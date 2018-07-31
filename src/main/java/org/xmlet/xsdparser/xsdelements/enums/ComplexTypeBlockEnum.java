package org.xmlet.xsdparser.xsdelements.enums;

import java.util.Arrays;
import java.util.List;

public enum ComplexTypeBlockEnum implements XsdEnum<ComplexTypeBlockEnum> {

    EXTENSION ("extension"),
    RESTRICTION ("restriction"),
    ALL ("#all");

    private final String value;

    ComplexTypeBlockEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getVariableName() {
        return "block";
    }

    @Override
    public ComplexTypeBlockEnum[] getValues() {
        return ComplexTypeBlockEnum.values();
    }

    @Override
    public List<String> getSupportedValues() {
        return Arrays.asList(EXTENSION.getValue(),
                             RESTRICTION.getValue(),
                             ALL.getValue());
    }
}
