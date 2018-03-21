package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.XsdAnnotatedElements;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

public abstract class XsdIntegerRestrictions extends XsdAnnotatedElements {

    private XsdElementVisitor xsdElementVisitor = new AnnotatedXsdElementVisitor() {
        @Override
        public XsdAbstractElement getOwner() {
            return XsdIntegerRestrictions.this;
        }
    };
    private boolean fixed;
    private int value;

    public XsdIntegerRestrictions(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public XsdElementVisitor getVisitor() {
        return xsdElementVisitor;
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        fixed = Boolean.parseBoolean(elementFieldsMap.getOrDefault(FIXED_TAG, "false"));
        value = Integer.parseInt(elementFieldsMap.getOrDefault(VALUE_TAG, "0"));
    }

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