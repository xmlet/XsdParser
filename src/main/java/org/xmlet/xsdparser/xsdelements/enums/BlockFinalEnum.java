package org.xmlet.xsdparser.xsdelements.enums;

import java.util.Arrays;
import java.util.List;

public enum BlockFinalEnum implements XsdEnum<BlockFinalEnum> {

    DEFAULT (""),
    EXTENSION ("extension"),
    RESTRICTION ("restriction"),
    SUBSTITUTION("substitution"),
    ALL ("#all");

    private final String value;

    BlockFinalEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getVariableName() {
        return "blockDefault";
    }

    @Override
    public BlockFinalEnum[] getValues() {
        return BlockFinalEnum.values();
    }

    @Override
    public List<String> getSupportedValues() {
        return Arrays.asList(DEFAULT.getValue(),
                             EXTENSION.getValue(),
                             RESTRICTION.getValue(),
                             SUBSTITUTION.getValue(),
                             ALL.getValue());
    }
}
