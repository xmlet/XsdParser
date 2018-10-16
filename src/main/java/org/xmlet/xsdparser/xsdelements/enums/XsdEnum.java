package org.xmlet.xsdparser.xsdelements.enums;

import java.util.List;

/**
 * A shared interface for all the {@link Enum} classes of this solution.
 * @param <T> The concrete type of the {@link Enum}.
 */
public interface XsdEnum<T> {

    /**
     * @return The name of the attribute that the {@link Enum} represents.
     */
    String getVariableName();

    /**
     * @return The values that the attribute can have.
     */
    List<String> getSupportedValues();

    /**
     * @return The values that the attribute can have, as members of the {@link Enum} type.
     */
    T[] getValues();

    /**
     * @return The concrete value of the current instance.
     */
    String getValue();

}
