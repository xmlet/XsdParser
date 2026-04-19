package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.XsdAnnotatedElements;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.function.Function;

/**
 * This class serves as a base to every different restriction that has its restricting parameter defined as an {@link String}.
 * Classes like {@link XsdPattern} or {@link XsdEnumeration} should extend this class.
 */
public class XsdStringRestrictions extends XsdAnnotatedElements{

    /**
     * The value of associated with a given restriction. This field has different meanings depending on the concrete
     * restriction, e.g. if the concrete class is {@link XsdEnumeration} this field means that the attribute which
     * has the restriction can only have the value that is specified in this field.
     */
    private String value;

    XsdStringRestrictions(@NotNull XsdParserCore parser, @NotNull Map<String, String> elementFieldsMapParam, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, elementFieldsMapParam, visitorFunction);

        value = attributesMap.get(VALUE_TAG);
        if (value == null){
            throw new ParsingException("The " + VALUE_TAG + " attribute is required.");
        }
    }

    /**
     * Compares two different objects of this type.
     * @param o1 The first object.
     * @param o2 The object to compare.
     * @return True if the value of both classes is different, False if the value is equal.
     */
    public static boolean hasDifferentValue(XsdStringRestrictions o1, XsdStringRestrictions o2) {
        if (o1 == null || o2 == null) {
            return false;
        }

        return !java.util.Objects.equals(o1.getValue(), o2.getValue());
    }

    public String getValue() {
        return value;
    }
}
