package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.AttributeValidations;
import org.xmlet.xsdparser.xsdelements.XsdAnnotatedElements;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * This class serves as a base to every different restriction that has its restricting parameter defined as an {@link Integer}.
 * Classes like {@link XsdMaxLength} or {@link XsdLength} should extend this class.
 * i.e. xsd:maxLength or xsd:length.
 */
public class XsdIntegerRestrictions extends XsdAnnotatedElements {

    private XsdAnnotatedElementsVisitor visitor = new XsdAnnotatedElementsVisitor(this);

    /**
     * Indicates if the value is fixed.
     */
    private boolean fixed;

    /**
     * The value of associated with a given restriction. This field has different meanings depending on the concrete
     * restriction, e.g. if the concrete class is {@link XsdLength} this field means that the attribute which
     * has the restriction can only have the length specified in this field..
     */
    protected int value;

    XsdIntegerRestrictions(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    @Override
    public XsdAnnotatedElementsVisitor getVisitor() {
        return visitor;
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        fixed = AttributeValidations.validateBoolean(elementFieldsMap.getOrDefault(FIXED_TAG, "false"));
    }

    /**
     * Compares two different objects of this type.
     * @param o1 The first object.
     * @param o2 The object to compare.
     * @return True if the value of both classes is different, False if the value is equal.
     */
    public static boolean hasDifferentValue(XsdIntegerRestrictions o1, XsdIntegerRestrictions o2) {
        if (o1 == null && o2 == null) {
            return false;
        }

        int o1Value = Integer.MAX_VALUE;
        int o2Value;

        if (o1 != null) {
            o1Value = o1.getValue();
        }

        if (o2 != null) {
            o2Value = o2.getValue();
            return o2Value == o1Value;
        }

        return false;
    }

    public int getValue() {
        return value;
    }

    public boolean isFixed() {
        return fixed;
    }
}