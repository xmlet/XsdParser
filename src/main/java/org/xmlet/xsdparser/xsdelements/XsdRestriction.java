package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdRestrictionsVisitor;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A class representing the xsd:restriction element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_restriction.asp">xsd:restriction description and usage at W3C</a>
 */
public class XsdRestriction extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:restriction";
    public static final String XS_TAG = "xs:restriction";

    /**
     * {@link XsdRestrictionsVisitor} instance which restricts the children elements of {@link XsdRestriction} to all
     * restricting XSD types. For a full list see {@link XsdRestrictionsVisitor}.
     */
    private XsdRestrictionsVisitor visitor = new XsdRestrictionsVisitor(this);

    /**
     * The {@link XsdSimpleType} instance of this {@link XsdRestriction} instance.
     */
    private XsdSimpleType simpleType;

    /**
     * A List of {@link XsdEnumeration} items, that represent a set of possible values for a given type.
     */
    private List<XsdEnumeration> enumeration = new ArrayList<>();

    /**
     * A {@link XsdFractionDigits} instance that specifies the number of fraction digits allowed in a numeric type.
     */
    private XsdFractionDigits fractionDigits;

    /**
     * A {@link XsdLength} instance that specifies the specific length of a List or String type.
     */
    private XsdLength length;

    /**
     * A {@link XsdMaxExclusive} instance that specifies the maxExclusive value for a numeric type.
     */
    private XsdMaxExclusive maxExclusive;

    /**
     * A {@link XsdMaxInclusive} instance that specifies the maxInclusive value for a numeric type.
     */
    private XsdMaxInclusive maxInclusive;

    /**
     * A {@link XsdMaxLength} instance that specifies the maxLength of a List or a String type.
     */
    private XsdMaxLength maxLength;

    /**
     * A {@link XsdMinExclusive} instance that specifies the minExclusive value for a numeric type.
     */
    private XsdMinExclusive minExclusive;

    /**
     * A {@link XsdMinInclusive} instance that specifies the minInclusive value for a numeric type.
     */
    private XsdMinInclusive minInclusive;

    /**
     * A {@link XsdMinLength} instance that specifies the minLength of a List or a String type.
     */
    private XsdMinLength minLength;

    /**
     * A {@link XsdPattern} instance that specifies a regex pattern that a String type should follow.
     */
    private XsdPattern pattern;

    /**
     * A {@link XsdTotalDigits} instance that specifies the total number of digits that a numeric type is allowed to have.
     */
    private XsdTotalDigits totalDigits;

    /**
     * A {@link XsdWhiteSpace} instance that specifies how the whitespace characters should be dealt with.
     */
    private XsdWhiteSpace whiteSpace;

    /**
     * The name of the type where this instance restrictions should be applied.
     */
    private String base;

    private XsdRestriction(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam){
        super.setFields(elementFieldsMapParam);

        this.base = elementFieldsMap.getOrDefault(BASE_TAG, base);
    }

    @Override
    public XsdRestrictionsVisitor getVisitor() {
        return visitor;
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public void replaceUnsolvedElements(NamedConcreteElement element) {
        super.replaceUnsolvedElements(element);

        visitor.replaceUnsolvedAttributes(element);
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node){
        return xsdParseSkeleton(node, new XsdRestriction(parser, convertNodeMap(node.getAttributes())));
    }

    @SuppressWarnings("unused")
    public Stream<XsdAttribute> getXsdAttributes() {
        return visitor.getXsdAttributes();
    }

    @SuppressWarnings("unused")
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

    public void setFractionDigits(XsdFractionDigits fractionDigits) {
        this.fractionDigits = fractionDigits;
    }

    public XsdLength getLength() {
        return length;
    }

    public void setLength(XsdLength length) {
        this.length = length;
    }

    public XsdMaxExclusive getMaxExclusive() {
        return maxExclusive;
    }

    public void setMaxExclusive(XsdMaxExclusive maxExclusive) {
        this.maxExclusive = maxExclusive;
    }

    public XsdMaxInclusive getMaxInclusive() {
        return maxInclusive;
    }

    public void setMaxInclusive(XsdMaxInclusive maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public XsdMaxLength getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(XsdMaxLength maxLength) {
        this.maxLength = maxLength;
    }

    public XsdMinExclusive getMinExclusive() {
        return minExclusive;
    }

    public void setMinExclusive(XsdMinExclusive minExclusive) {
        this.minExclusive = minExclusive;
    }

    public XsdMinInclusive getMinInclusive() {
        return minInclusive;
    }

    public void setMinInclusive(XsdMinInclusive minInclusive) {
        this.minInclusive = minInclusive;
    }

    public XsdMinLength getMinLength() {
        return minLength;
    }

    public void setMinLength(XsdMinLength minLength) {
        this.minLength = minLength;
    }

    public XsdPattern getPattern() {
        return pattern;
    }

    public void setPattern(XsdPattern pattern) {
        this.pattern = pattern;
    }

    public XsdTotalDigits getTotalDigits() {
        return totalDigits;
    }

    public void setTotalDigits(XsdTotalDigits totalDigits) {
        this.totalDigits = totalDigits;
    }

    public XsdWhiteSpace getWhiteSpace() {
        return whiteSpace;
    }

    public void setWhiteSpace(XsdWhiteSpace whiteSpace) {
        this.whiteSpace = whiteSpace;
    }

    public void add(XsdEnumeration enumerationMember) {
        enumeration.add(enumerationMember);
    }

    public void setSimpleType(XsdSimpleType simpleType) {
        this.simpleType = simpleType;
    }
}
