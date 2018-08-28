package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.AttributeValidations;
import org.xmlet.xsdparser.xsdelements.XsdAnnotatedElements;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * This class serves as a base to every different restriction that has its restricting parameter defined as a Double
 * i.e. xsd:maxInclusive or xsd:maxExclusive.
 */
public abstract class XsdDoubleRestrictions extends XsdAnnotatedElements {

    private XsdAnnotatedElementsVisitor visitor = new XsdAnnotatedElementsVisitor(this);

    private String restrictionName;

    private boolean fixed;
    private double value;

    XsdDoubleRestrictions(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam, String restrictionName) {
        super(parser, elementFieldsMapParam);

        this.restrictionName = restrictionName;
    }

    @Override
    public XsdAnnotatedElementsVisitor getVisitor() {
        return visitor;
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        fixed = AttributeValidations.validateBoolean(elementFieldsMap.getOrDefault(FIXED_TAG, "false"));
        value = AttributeValidations.validateRequiredDouble(restrictionName, VALUE_TAG, elementFieldsMap.get(VALUE_TAG));
    }

    /**
     * Compares two different objects of this type.
     * @param o1 The first object.
     * @param o2 The object to compare.
     * @return True if the value of both classes is different, False if the value is equal.
     */
    public static boolean hasDifferentValue(XsdDoubleRestrictions o1, XsdDoubleRestrictions o2) {
        if (o1 == null && o2 == null) {
            return false;
        }

        double o1Value = Double.MAX_VALUE;
        double o2Value;

        if (o1 != null) {
            o1Value = o1.getValue();
        }

        if (o2 != null) {
            o2Value = o2.getValue();
            return o2Value == o1Value;
        }

        return false;
    }

    public double getValue() {
        return value;
    }

    public boolean isFixed() {
        return fixed;
    }
}