package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class XsdRestriction extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:restriction";
    public static final String XS_TAG = "xs:restriction";

    private RestrictionXsdElementVisitor visitor = new RestrictionXsdElementVisitor();

    private XsdSimpleType simpleType;

    private List<XsdEnumeration> enumeration = new ArrayList<>();
    private XsdFractionDigits fractionDigits;
    private XsdLength length;
    private XsdMaxExclusive maxExclusive;
    private XsdMaxInclusive maxInclusive;
    private XsdMaxLength maxLength;
    private XsdMinExclusive minExclusive;
    private XsdMinInclusive minInclusive;
    private XsdMinLength minLength;
    private XsdPattern pattern;
    private XsdTotalDigits totalDigits;
    private XsdWhiteSpace whiteSpace;

    private String base;

    private XsdRestriction(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam){
        super.setFields(elementFieldsMapParam);

        this.base = elementFieldsMap.getOrDefault(BASE_TAG, base);
    }

    @Override
    public XsdElementVisitor getVisitor() {
        return visitor;
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        super.accept(xsdElementVisitor);
        xsdElementVisitor.visit(this);
    }

    @Override
    public void replaceUnsolvedElements(NamedConcreteElement element) {
        super.replaceUnsolvedElements(element);

        visitor.replaceUnsolvedAttributes(element);
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdRestriction(convertNodeMap(node.getAttributes())));
    }

    public Stream<XsdAttribute> getXsdAttributes() {
        return visitor.getXsdAttributes();
    }

    public Stream<XsdAttributeGroup> getXsdAttributeGroup() {
        return visitor.getXsdAttributeGroup();
    }

    public XsdSimpleType getSimpleType() {
        return simpleType;
    }

    public String getBase() {
        return base;
    }

    public List<XsdEnumeration> getEnumeration() {
        return enumeration;
    }

    void setEnumeration(List<XsdEnumeration> enumeration){
        this.enumeration = enumeration;
    }

    public XsdFractionDigits getFractionDigits() {
        return fractionDigits;
    }

    void setFractionDigits(XsdFractionDigits fractionDigits) {
        this.fractionDigits = fractionDigits;
    }

    public XsdLength getLength() {
        return length;
    }

    void setLength(XsdLength length) {
        this.length = length;
    }

    public XsdMaxExclusive getMaxExclusive() {
        return maxExclusive;
    }

    void setMaxExclusive(XsdMaxExclusive maxExclusive) {
        this.maxExclusive = maxExclusive;
    }

    public XsdMaxInclusive getMaxInclusive() {
        return maxInclusive;
    }

    void setMaxInclusive(XsdMaxInclusive maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public XsdMaxLength getMaxLength() {
        return maxLength;
    }

    void setMaxLength(XsdMaxLength maxLength) {
        this.maxLength = maxLength;
    }

    public XsdMinExclusive getMinExclusive() {
        return minExclusive;
    }

    void setMinExclusive(XsdMinExclusive minExclusive) {
        this.minExclusive = minExclusive;
    }

    public XsdMinInclusive getMinInclusive() {
        return minInclusive;
    }

    void setMinInclusive(XsdMinInclusive minInclusive) {
        this.minInclusive = minInclusive;
    }

    public XsdMinLength getMinLength() {
        return minLength;
    }

    void setMinLength(XsdMinLength minLength) {
        this.minLength = minLength;
    }

    public XsdPattern getPattern() {
        return pattern;
    }

    void setPattern(XsdPattern pattern) {
        this.pattern = pattern;
    }

    public XsdTotalDigits getTotalDigits() {
        return totalDigits;
    }

    void setTotalDigits(XsdTotalDigits totalDigits) {
        this.totalDigits = totalDigits;
    }

    public XsdWhiteSpace getWhiteSpace() {
        return whiteSpace;
    }

    void setWhiteSpace(XsdWhiteSpace whiteSpace) {
        this.whiteSpace = whiteSpace;
    }

    class RestrictionXsdElementVisitor extends AttributesVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdRestriction.this;
        }

        @Override
        public void visit(XsdEnumeration element) {
            super.visit(element);

            XsdRestriction.this.enumeration.add(element);
        }

        @Override
        public void visit(XsdFractionDigits element) {
            super.visit(element);

            XsdRestriction.this.fractionDigits = element;
        }

        @Override
        public void visit(XsdLength element) {
            super.visit(element);

            XsdRestriction.this.length = element;
        }

        @Override
        public void visit(XsdMaxExclusive element) {
            super.visit(element);

            XsdRestriction.this.maxExclusive = element;
        }

        @Override
        public void visit(XsdMaxInclusive element) {
            super.visit(element);

            XsdRestriction.this.maxInclusive = element;
        }

        @Override
        public void visit(XsdMaxLength element) {
            super.visit(element);

            XsdRestriction.this.maxLength = element;
        }

        @Override
        public void visit(XsdMinExclusive element) {
            super.visit(element);

            XsdRestriction.this.minExclusive = element;
        }

        @Override
        public void visit(XsdMinInclusive element) {
            super.visit(element);

            XsdRestriction.this.minInclusive = element;
        }

        @Override
        public void visit(XsdMinLength element) {
            super.visit(element);

            XsdRestriction.this.minLength = element;
        }

        @Override
        public void visit(XsdPattern element) {
            super.visit(element);

            XsdRestriction.this.pattern = element;
        }

        @Override
        public void visit(XsdTotalDigits element) {
            super.visit(element);

            XsdRestriction.this.totalDigits = element;
        }

        @Override
        public void visit(XsdWhiteSpace element) {
            super.visit(element);

            XsdRestriction.this.whiteSpace = element;
        }

        @Override
        public void visit(XsdSimpleType element) {
            super.visit(element);

            XsdRestriction.this.simpleType = element;
        }
    }
}
