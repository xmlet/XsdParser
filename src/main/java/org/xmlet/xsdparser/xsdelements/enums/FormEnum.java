package org.xmlet.xsdparser.xsdelements.enums;

import java.util.Arrays;
import java.util.List;

/**
 * An {@link Enum} with all the possible values for the form attribute.
 */
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
