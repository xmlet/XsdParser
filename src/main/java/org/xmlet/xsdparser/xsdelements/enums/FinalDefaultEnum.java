package org.xmlet.xsdparser.xsdelements.enums;

import org.xmlet.xsdparser.xsdelements.XsdSchema;

import java.util.Arrays;
import java.util.List;

/**
 * An {@link Enum} with all the possible values for the finalDefault attribute of {@link XsdSchema}.
 */
public enum FinalDefaultEnum implements XsdEnum<FinalDefaultEnum> {

    DEFAULT (""),
    EXTENSION ("extension"),
    RESTRICTION ("restriction"),
    LIST("list"),
    UNION("union"),
    ALL ("#all");

    private final String value;

    FinalDefaultEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getVariableName() {
        return "finalDefault";
    }

    @Override
    public FinalDefaultEnum[] getValues() {
        return FinalDefaultEnum.values();
    }

    @Override
    public List<String> getSupportedValues() {
        return Arrays.asList(DEFAULT.getValue(),
                             EXTENSION.getValue(),
                             RESTRICTION.getValue(),
                             LIST.getValue(),
                             UNION.getValue(),
                             ALL.getValue());
    }
}
