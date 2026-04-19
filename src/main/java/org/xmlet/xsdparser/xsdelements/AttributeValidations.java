package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.enums.XsdEnum;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

import java.util.Arrays;
import java.util.Optional;

import static org.xmlet.xsdparser.xsdelements.XsdAbstractElement.MAX_OCCURS_TAG;
import static org.xmlet.xsdparser.xsdelements.XsdAbstractElement.MIN_OCCURS_TAG;

public class AttributeValidations {

    private AttributeValidations(){ }

    /**
     * Verifies if a given value is present in a given {@link Enum} type. All whitespace-separated tokens in
     * {@code value} are validated against {@code instance}; unsupported tokens cause a {@link ParsingException}.
     * <p>
     * Returns only the FIRST matching enum value. For attributes that semantically represent a token list
     * (e.g. {@code blockDefault}, {@code finalDefault}, {@code block}, {@code final}) prefer
     * {@link #validateEnumTokenList(XsdEnum, String)} which preserves the full normalized list.
     * <p>
     * Null returns represent "no value" (attribute absent or explicitly empty without an empty-string enum entry).
     * @param instance An instance of the concrete {@link Enum} type that is expected to contain the {@code value} received.
     * @param value The value that is expected to be present in the received {@link Enum} type.
     * @param <T> The concrete type of the {@link Enum} type.
     * @return The first matching {@link Enum} entry, or {@code null} when {@code value} is null or empty and the enum
     *         does not declare an empty-string member.
     */
    public static <T extends XsdEnum> T belongsToEnum(final XsdEnum<T> instance, final String value){
        if (value == null){
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()){
            return Arrays.stream(instance.getValues())
                    .filter(enumField -> enumField.getValue().equals(""))
                    .findFirst()
                    .orElse(null);
        }

        String[] tokens = trimmed.split("\\s+");
        T first = null;
        for (String token : tokens) {
            Optional<T> enumValue = Arrays.stream(instance.getValues()).filter(enumField -> enumField.getValue().equals(token)).findFirst();
            if (!enumValue.isPresent()) {
                throwUnsupportedEnumValue(instance, token);
            }
            if (first == null) first = enumValue.get();
        }
        return first;
    }

    /**
     * Validates each whitespace-separated token in {@code value} against the supplied {@link XsdEnum} instance and
     * returns the normalized token list (single-space separated). Returns {@code null} when {@code value} is null.
     */
    public static <T extends XsdEnum> String validateEnumTokenList(final XsdEnum<T> instance, final String value){
        if (value == null){
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()){
            Optional<T> emptyEnum = Arrays.stream(instance.getValues())
                    .filter(enumField -> enumField.getValue().equals(""))
                    .findFirst();
            if (!emptyEnum.isPresent()){
                throwUnsupportedEnumValue(instance, "");
            }
            return "";
        }

        String[] tokens = trimmed.split("\\s+");
        StringBuilder normalized = new StringBuilder();
        for (String token : tokens) {
            boolean known = Arrays.stream(instance.getValues()).anyMatch(enumField -> enumField.getValue().equals(token));
            if (!known){
                throwUnsupportedEnumValue(instance, token);
            }
            if (normalized.length() > 0) normalized.append(' ');
            normalized.append(token);
        }
        return normalized.toString();
    }

    private static void throwUnsupportedEnumValue(XsdEnum<?> instance, String token){
        StringBuilder possibleValues = new StringBuilder();
        instance.getSupportedValues().forEach(supportedValue -> possibleValues.append(supportedValue).append(", "));
        String values = possibleValues.toString();
        values = values.substring(0, values.length() - 2);
        throw new ParsingException("The attribute " + instance.getVariableName() + " doesn't support the value \"" + token + "\".\n" +
                "The possible values for the " + instance.getVariableName() + " attribute are:\n" +
                values);
    }

    /**
     * Checks if the maxOccurs attribute is unbounded or an {@link Integer} value.
     * @param value The possible maxOccurs value.
     * @param elementName The name of the element containing the maxOccurs attribute.
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
     * Validates if a given String is a non negative {@link Integer}. Throws an exception if the {@link String} isn't a non
     * negative {@link Integer}.
     * @param elementName The element name containing the field with the {@link Integer} value.
     * @param attributeName The name of the attribute with the {@link Integer}.
     * @param value The value to be parsed to a {@link Integer} object.
     * @return The parsed {@link Integer} value.
     */
    static Integer validateNonNegativeInteger(String elementName, String attributeName, String value){
        try {
            value = value.trim();

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
     * Validates if a given String is a non negative {@link Integer}. Throws an exception if the {@link String} isn't a
     * non negative {@link Integer}.
     * @param elementName The element name containing the field with the {@link Integer} value.
     * @param attributeName The name of the attribute with the {@link Integer}.
     * @param value The value to be parsed to a {@link Integer} object.
     * @return The parsed {@link Integer} value.
     */
    public static Integer validateRequiredNonNegativeInteger(String elementName, String attributeName, String value){
        if (value == null) throw new ParsingException(attributeMissingMessage(elementName, attributeName));

        return validateNonNegativeInteger(elementName, attributeName, value);
    }

    /**
     * Validates if a given String is a positive {@link Integer}. Throws an exception if the {@link String} isn't a
     * positive {@link Integer}.
     * @param elementName The element name containing the field with the {@link Integer} value.
     * @param attributeName The name of the attribute with the {@link Integer} type.
     * @param value The value to be parsed to a {@link Integer} object.
     * @return The parsed {@link Integer} value.
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
     * Validates if a given String is a positive {@link Integer}. Throws an exception if the {@link String} isn't a
     * positive {@link Integer}.
     * @param elementName The element name containing the field with the {@link Integer} value.
     * @param attributeName The name of the attribute with the {@link Integer} type.
     * @param value The value to be parsed to a {@link Integer} object.
     * @return The parsed {@link Integer} value.
     */
    public static Integer validateRequiredPositiveInteger(String elementName, String attributeName, String value){
        if (value == null) throw new ParsingException(attributeMissingMessage(elementName, attributeName));

        return validatePositiveInteger(elementName, attributeName, value);
    }

    /**
     * Validates the value of a {@code namespace} attribute on {@code xs:any} / {@code xs:anyAttribute}. Per
     * XSD 1.0 §3.10.2 the value is either {@code ##any} / {@code ##other} (alone), or a whitespace-separated list
     * of items where each item is {@code ##targetNamespace}, {@code ##local}, or a URI reference.
     */
    static String validateAnyNamespace(String elementName, String value){
        if (value == null) return null;
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return trimmed;
        if (trimmed.equals("##any") || trimmed.equals("##other")) return trimmed;
        for (String token : trimmed.split("\\s+")) {
            if (token.equals("##any") || token.equals("##other")){
                throw new ParsingException(elementName + " element: " + token + " cannot appear in a namespace token list.");
            }
            if (token.equals("##targetNamespace") || token.equals("##local")) continue;
            try {
                new java.net.URI(token);
            } catch (java.net.URISyntaxException e){
                throw new ParsingException(elementName + " element: namespace token \"" + token + "\" is not a valid URI reference.");
            }
        }
        return trimmed;
    }

    /**
     * Validates that {@code minOccurs} is less than or equal to {@code maxOccurs}. Both have already been validated
     * individually as non-negative integers / {@code "unbounded"}; this only checks the relation.
     */
    static void validateOccurrenceRange(String elementName, Integer minOccurs, String maxOccurs){
        if (minOccurs == null || maxOccurs == null) return;
        if (maxOccurs.equals("unbounded")) return;
        int max;
        try {
            max = Integer.parseInt(maxOccurs.trim());
        } catch (NumberFormatException e){
            return;
        }
        if (minOccurs > max){
            throw new ParsingException("The " + elementName + " " + MIN_OCCURS_TAG + " attribute (" + minOccurs + ") must be less than or equal to " + MAX_OCCURS_TAG + " (" + max + ").");
        }
    }

    /**
     * Validates that {@code value} is a legal XML NCName (XSD §3.3.x {@code name} attribute): starts with a letter
     * or underscore, contains only NCName characters, and does not contain a colon.
     */
    static void validateNCName(String elementName, String value){
        if (value == null) return;
        if (value.isEmpty()){
            throw new ParsingException(elementName + " element: " + XsdAbstractElement.NAME_TAG + " attribute must be a non-empty NCName.");
        }
        if (value.indexOf(':') >= 0){
            throw new ParsingException(elementName + " element: " + XsdAbstractElement.NAME_TAG + " attribute must be an NCName (cannot contain ':'): \"" + value + "\".");
        }
        char first = value.charAt(0);
        if (!(Character.isLetter(first) || first == '_')){
            throw new ParsingException(elementName + " element: " + XsdAbstractElement.NAME_TAG + " attribute must start with a letter or underscore: \"" + value + "\".");
        }
        for (int i = 1; i < value.length(); i++){
            char c = value.charAt(i);
            if (!(Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == '.')){
                throw new ParsingException(elementName + " element: " + XsdAbstractElement.NAME_TAG + " attribute contains an illegal NCName character: \"" + value + "\".");
            }
        }
    }

    public static Boolean validateBoolean(String value){
        if (value == null) return null;
        String trimmed = value.trim();
        if (trimmed.equals("true") || trimmed.equals("1")) return Boolean.TRUE;
        if (trimmed.equals("false") || trimmed.equals("0")) return Boolean.FALSE;
        throw new ParsingException("Invalid xs:boolean value \"" + value + "\". Valid values are: true, false, 0, 1.");
    }

    /**
     * Validates if a given {@link String} is a {@link Double}. Throws an exception if the {@link String} isn't a
     * {@link Double}.
     * @param elementName The element name containing the field with the {@link Double} value.
     * @param attributeName The name of the attribute with the type {@link Double}.
     * @param value The value to be parsed to a {@link Double} object.
     * @return The parsed {@link Double} value.
     */
    private static Double validateDouble(String elementName, String attributeName, String value){
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e){
            throw new ParsingException("The " + elementName + " " + attributeName + "  attribute should be a numeric value.");
        }
    }

    /**
     * Validates if a given {@link String} is a {@link Double}. Throws an exception if the {@link String} isn't a {@link Double}.
     * @param elementName The element name containing the field with the {@link Double} value.
     * @param attributeName The name of the attribute with the type {@link Double}.
     * @param value The value to be parsed to a {@link Double} object.
     * @return The parsed {@link Double} value.
     */
    public static Double validateRequiredDouble(String elementName, String attributeName, String value){
        if (value == null) throw new ParsingException(attributeMissingMessage(elementName, attributeName));

        return validateDouble(elementName, attributeName, value);
    }

    /**
     * Obtains the default value of the {@link XsdSchema#attributeFormDefault} attribute by iterating in the  element tree
     * by going from {@link XsdAbstractElement#parent} to {@link XsdAbstractElement#parent} until reaching the top level
     * element.
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
     * Obtains the default value of the {@link XsdSchema#finalDefault} attribute by iterating in the element tree by
     * going from {@link XsdAbstractElement#parent} to {@link XsdAbstractElement#parent} until reaching the top level
     * element.
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
     * Obtains the default value of the {@link XsdSchema#blockDefault} attribute by iterating in the element tree by
     * going from {@link XsdAbstractElement#parent} to {@link XsdAbstractElement#parent} until reaching the top level
     * element.
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

    private static String attributeMissingMessage(String elementName, String attributeName){
        return "The " + elementName + " " + attributeName + " is required to have a value attribute.";
    }
}
