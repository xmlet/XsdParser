package org.xmlet.xsdparser.xsdelements.enums;

import java.util.Arrays;
import java.util.List;

/**
 * An {@link Enum} with all the possible values for the usage attribute.
 */
public enum UsageEnum implements XsdEnum<UsageEnum> {

    REQUIRED ("required"),
    PROHIBITED ("prohibited"),
    OPTIONAL ("optional");

    private final String value;

    UsageEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getVariableName() {
        return "use";
    }

    @Override
    public UsageEnum[] getValues() {
        return UsageEnum.values();
    }

    @Override
    public List<String> getSupportedValues() {
        return Arrays.asList(REQUIRED.getValue(),
                             PROHIBITED.getValue(),
                             OPTIONAL.getValue());
    }
}
