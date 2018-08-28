package org.xmlet.xsdparser.xsdelements.enums;

import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

import java.util.Arrays;
import java.util.Optional;

public class EnumUtils {

    public static <T extends XsdEnum> T belongsToEnum(final XsdEnum<T> instance, final String value){
        if (value == null){
            return null;
        }

        Optional<T> enumValue = Arrays.stream(instance.getValues()).filter(enumField -> enumField.getValue().equals(value)).findFirst();

        if (enumValue.isPresent()){
            return enumValue.get();
        } else {
            StringBuilder possibleValues = new StringBuilder();

            instance.getSupportedValues().forEach(supportedValue -> possibleValues.append(supportedValue).append(", "));

            String values = possibleValues.toString();
            values = values.substring(0, values.length() - 2);

            throw new ParsingException("The attribute " + instance.getVariableName() + " doesn't support the value \"" + value + "\".\n" +
                    "The possible values for the " + instance.getVariableName() + " attribute are:\n" +
                    values);
        }
    }

}
