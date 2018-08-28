package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

import static org.xmlet.xsdparser.xsdelements.XsdAbstractElement.MAX_OCCURS_TAG;

public class AttributeValidations {

    /**
     * Checks if the maxOccurs attribute is unbounded or an Integer value.
     * @param value The possible maxOccurs value.
     * @return The validated maxOccurs value.
     */
    static String maxOccursValidation(String elementName, String value){
        if (value.equals("unbounded")){
            return value;
        }

        validateNonNegativeInteger(elementName, MAX_OCCURS_TAG, value);

        return value;
    }

    /**
     * Validates if a given String is a non negative Integer. Throws an exception if the String isn't a non negative Integer.
     * @param elementName The element name containing the field with the Integer value.
     * @param attributeName The name of the attribute with the Integer.
     * @param value The value to be parsed to a Integer object.
     * @return The parsed Integer value.
     */
    static Integer validateNonNegativeInteger(String elementName, String attributeName, String value){
        try {
            int intValue = Integer.parseInt(value);

            if (intValue < 0){
                throw new ParsingException("The " + elementName + " " + attributeName + " attribute should be a non negative integer. (greater or equal than 0)");
            }

            return intValue;
        } catch (NumberFormatException e){
            throw new ParsingException("The " + elementName + " " + attributeName + "  attribute should be a non negative integer.");
        }
    }

    /**
     * Validates if a given String is a non negative Integer. Throws an exception if the String isn't a non negative Integer.
     * @param elementName The element name containing the field with the Integer value.
     * @param attributeName The name of the attribute with the Integer.
     * @param value The value to be parsed to a Integer object.
     * @return The parsed Integer value.
     */
    public static Integer validateRequiredNonNegativeInteger(String elementName, String attributeName, String value){
        if (value == null) throw new ParsingException("The " + elementName + " " + attributeName + " is required to have a value attribute.");

        return validateNonNegativeInteger(elementName, attributeName, value);
    }

    /**
     * Validates if a given String is a positive Integer. Throws an exception if the String isn't a positive Integer.
     * @param elementName The element name containing the field with the Integer value.
     * @param attributeName The name of the attribute with the Integer type.
     * @param value The value to be parsed to a Integer object.
     * @return The parsed Integer value.
     */
    private static Integer validatePositiveInteger(String elementName, String attributeName, String value){
        try {
            int intValue = Integer.parseInt(value);

            if (intValue <= 0){
                throw new ParsingException("The " + elementName + " " + attributeName + "  attribute should be a positive integer. (greater than 0)");
            }

            return intValue;
        } catch (NumberFormatException e){
            throw new ParsingException("The " + elementName + " " + attributeName + "  attribute should be a positive integer.");
        }
    }

    /**
     * Validates if a given String is a positive Integer. Throws an exception if the String isn't a positive Integer.
     * @param elementName The element name containing the field with the Integer value.
     * @param attributeName The name of the attribute with the Integer type.
     * @param value The value to be parsed to a Integer object.
     * @return The parsed Integer value.
     */
    public static Integer validateRequiredPositiveInteger(String elementName, String attributeName, String value){
        if (value == null) throw new ParsingException("The " + elementName + " " + attributeName + " is required to have a value attribute.");

        return validatePositiveInteger(elementName, attributeName, value);
    }

    public static Boolean validateBoolean(String value){
         return Boolean.parseBoolean(value);
    }

    /**
     * Validates if a given String is a Double. Throws an exception if the String isn't a double.
     * @param elementName The element name containing the field with the Double value.
     * @param attributeName The name of the attribute with the type Double.
     * @param value The value to be parsed to a Double object.
     * @return The parsed double value.
     */
    private static Double validateDouble(String elementName, String attributeName, String value){
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e){
            throw new ParsingException("The " + elementName + " " + attributeName + "  attribute should be a numeric value.");
        }
    }

    /**
     * Validates if a given String is a Double. Throws an exception if the String isn't a double.
     * @param elementName The element name containing the field with the Double value.
     * @param attributeName The name of the attribute with the type Double.
     * @param value The value to be parsed to a Double object.
     * @return The parsed double value.
     */
    public static Double validateRequiredDouble(String elementName, String attributeName, String value){
        if (value == null) throw new ParsingException("The " + elementName + " " + attributeName + " is required to have a value attribute.");

        return validateDouble(elementName, attributeName, value);
    }

    /**
     * Obtains the default value of the form attribute from the top level {@link XsdSchema} element by iterating in the
     * element tree by going from parent to parent until reaching the top level element.
     * @param parent The parent of the element requesting the default form value.
     * @return The default value for the form attribute.
     */
    static String getFormDefaultValue(XsdAbstractElement parent) {
        if (parent == null) return null;

        if (parent instanceof XsdSchema){
            return ((XsdSchema)parent).getElementFormDefault();
        }

        return getFormDefaultValue(parent.getParent());
    }

    /**
     * Obtains the default value of the final attribute from the top level {@link XsdSchema} element by iterating in the
     * element tree by going from parent to parent until reaching the top level element.
     * @param parent The parent of the element requesting the default final value.
     * @return The default value for the final attribute.
     */
    static String getFinalDefaultValue(XsdAbstractElement parent) {
        if (parent == null) return null;

        if (parent instanceof XsdSchema){
            return ((XsdSchema)parent).getFinalDefault();
        }

        return getFinalDefaultValue(parent.getParent());
    }

    /**
     * Obtains the default value of the block attribute from the top level {@link XsdSchema} element by iterating in the
     * element tree by going from parent to parent until reaching the top level element.
     * @param parent The parent of the element requesting the default block value.
     * @return The default value for the block attribute.
     */
    static String getBlockDefaultValue(XsdAbstractElement parent) {
        if (parent == null) return null;

        if (parent instanceof XsdSchema){
            return ((XsdSchema)parent).getBlockDefault();
        }

        return getBlockDefaultValue(parent.getParent());
    }
}
