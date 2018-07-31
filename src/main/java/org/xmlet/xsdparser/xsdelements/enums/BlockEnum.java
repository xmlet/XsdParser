package org.xmlet.xsdparser.xsdelements.enums;

import java.util.Arrays;
import java.util.List;

public enum BlockEnum implements XsdEnum<BlockEnum> {

    EXTENSION ("extension"),
    RESTRICTION ("restriction"),
    SUBSTITUTION("substitution"),
    ALL ("#all");

    private final String value;

    BlockEnum(String value) {
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
    public BlockEnum[] getValues() {
        return BlockEnum.values();
    }

    @Override
    public List<String> getSupportedValues() {
        return Arrays.asList(EXTENSION.getValue(),
                             RESTRICTION.getValue(),
                             SUBSTITUTION.getValue(),
                             ALL.getValue());
    }
}
