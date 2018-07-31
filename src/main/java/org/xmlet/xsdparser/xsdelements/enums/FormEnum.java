package org.xmlet.xsdparser.xsdelements.enums;

import java.util.Arrays;
import java.util.List;

public enum FormEnum implements XsdEnum<FormEnum> {

    QUALIFIED ("qualified"),
    UNQUALIFIED ("unqualified");

    private final String value;

    FormEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getVariableName() {
        return "form/elementFormDefault/attributeFormDefault";
    }

    @Override
    public FormEnum[] getValues() {
        return FormEnum.values();
    }

    @Override
    public List<String> getSupportedValues() {
        return Arrays.asList(QUALIFIED.getValue(),
                             UNQUALIFIED.getValue());
    }
}
