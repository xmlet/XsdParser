package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ConfigEntryData;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdRestrictionsVisitor;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.xmlet.xsdparser.core.XsdParserCore.getParseMappers;

/**
 * A class representing the xsd:restriction element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_restriction.asp">xsd:restriction description and usage at W3C</a>
 */
public class XsdRestriction extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:restriction";
    public static final String XS_TAG = "xs:restriction";
    public static final String TAG = "restriction";

    /**
     * The {@link XsdSimpleType} instance of this {@link XsdRestriction} instance.
     */
    private ReferenceBase base;

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
     * A List of{@link XsdPattern} items, that represent a set of pattern that a String type should follow.
     */
    private List<XsdPattern> pattern = new ArrayList<>();

    /**
     * A {@link XsdTotalDigits} instance that specifies the total number of digits that a numeric type is allowed to have.
     */
    private XsdTotalDigits totalDigits;

    /**
     * A {@link XsdWhiteSpace} instance that specifies how the whitespace characters should be dealt with.
     */
    private XsdWhiteSpace whiteSpace;

    private ReferenceBase group;

    private XsdAll all;

    private XsdChoice choice;

    private XsdSequence sequence;

    /**
     * The name of the type where this instance restrictions should be applied.
     */
    private String baseString;

    private XsdRestriction(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, attributesMap, visitorFunction);

        String baseValue = attributesMap.getOrDefault(BASE_TAG, null);
        this.baseString = baseValue;

        if (baseValue != null){
            if (XsdParserCore.getXsdTypesToJava().containsKey(baseValue)){
                HashMap<String, String> attributes = new HashMap<>();
                attributes.put(NAME_TAG, baseValue);
                this.base = ReferenceBase.createFromXsd(new XsdBuiltInDataType(parser, attributes, this));
            } else {
                Map<String, ConfigEntryData> parseMappers = getParseMappers();
                ConfigEntryData config = parseMappers.getOrDefault(XsdElement.XSD_TAG, parseMappers.getOrDefault(XsdElement.XS_TAG, null));

                if (config == null){
                    throw new ParsingException("Invalid Parsing Configuration for XsdElement.");
                }

                this.base = new UnsolvedReference(baseValue, new XsdElement(this, this.parser, new HashMap<>(), config.visitorFunction));
                parser.addUnsolvedReference((UnsolvedReference) this.base);
            }
        }
    }

    public XsdRestriction(XsdAbstractElement parent, @NotNull XsdParserCore parser, @NotNull Map<String, String> elementFieldsMapParam, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        this(parser, elementFieldsMapParam, visitorFunction);
        setParent(parent);
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public void replaceUnsolvedElements(NamedConcreteElement element) {
        super.replaceUnsolvedElements(element);

        ((XsdRestrictionsVisitor)visitor).replaceUnsolvedAttributes(parser, element, this);

        XsdNamedElements elem = element.getElement();

        boolean isComplexOrSimpleType = elem instanceof XsdComplexType || elem instanceof XsdSimpleType;

        if (this.base instanceof UnsolvedReference && isComplexOrSimpleType && compareReference(element, (UnsolvedReference) this.base)){
            this.base = element;
        }

        if (this.group instanceof UnsolvedReference && this.group.getElement() instanceof XsdGroup &&
                elem instanceof XsdGroup && compareReference(element, (UnsolvedReference) this.group)){
            this.group = element;
        }
    }

    /**
     * Performs a copy of the current object for replacing purposes. The cloned objects are used to replace
     * {@link UnsolvedReference} objects in the reference solving process.
     * @param placeHolderAttributes The additional attributes to add to the clone.
     * @return A copy of the object from which is called upon.
     */
    @Override
    public XsdRestriction clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(attributesMap);

        XsdRestriction elementCopy = new XsdRestriction(this.parser, placeHolderAttributes, visitorFunction);

        if (this.enumeration != null){
            elementCopy.enumeration = this.enumeration.stream().map(enumerationObj -> (XsdEnumeration) enumerationObj.clone(enumerationObj.getAttributesMap(), elementCopy)).collect(Collectors.toList());
        }

        if (this.fractionDigits != null){
            elementCopy.fractionDigits = (XsdFractionDigits) this.fractionDigits.clone(fractionDigits.getAttributesMap(), elementCopy);
        }

        if (this.length != null){
            elementCopy.length = (XsdLength) this.length.clone(length.getAttributesMap(), elementCopy);
        }

        if (this.maxExclusive != null){
            elementCopy.maxExclusive = (XsdMaxExclusive) this.maxExclusive.clone(maxExclusive.getAttributesMap(), elementCopy);
        }

        if (this.maxInclusive != null){
            elementCopy.maxInclusive = (XsdMaxInclusive) this.maxInclusive.clone(maxInclusive.getAttributesMap(), elementCopy);
        }

        if (this.maxLength != null){
            elementCopy.maxLength = (XsdMaxLength) this.maxLength.clone(maxLength.getAttributesMap(), elementCopy);
        }

        if (this.minExclusive != null){
            elementCopy.minExclusive = (XsdMinExclusive) this.minExclusive.clone(minExclusive.getAttributesMap(), elementCopy);
        }

        if (this.minInclusive != null){
            elementCopy.minInclusive = (XsdMinInclusive) this.minInclusive.clone(minInclusive.getAttributesMap(), elementCopy);
        }

        if (this.minLength != null){
            elementCopy.minLength = (XsdMinLength) this.minLength.clone(minLength.getAttributesMap(), elementCopy);
        }

        if (this.pattern != null){
            elementCopy.pattern = this.pattern.stream().map(patternObj -> (XsdPattern) patternObj.clone(patternObj.getAttributesMap(), elementCopy)).collect(Collectors.toList());
        }

        if (this.totalDigits != null){
            elementCopy.totalDigits = (XsdTotalDigits) this.totalDigits.clone(totalDigits.getAttributesMap(), elementCopy);
        }

        if (this.whiteSpace != null){
            elementCopy.whiteSpace = (XsdWhiteSpace) this.whiteSpace.clone(whiteSpace.getAttributesMap(), elementCopy);
        }

        if (this.all != null){
            elementCopy.all = (XsdAll) this.all.clone(this.all.getAttributesMap(), elementCopy);
        }

        if (this.choice != null){
            elementCopy.choice = (XsdChoice) this.choice.clone(this.choice.getAttributesMap(), elementCopy);
        }

        if (this.sequence != null){
            elementCopy.sequence = (XsdSequence) this.sequence.clone(this.sequence.getAttributesMap(), elementCopy);
        }

        if (this.group != null){
            elementCopy.group = ReferenceBase.clone(this.parser, this.group, elementCopy);
        }

        elementCopy.parent = null;
        elementCopy.base = this.base;

        return elementCopy;
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdRestriction(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    @SuppressWarnings("unused")
    public Stream<XsdAttribute> getXsdAttributes() {
        return ((XsdRestrictionsVisitor)visitor).getXsdAttributes();
    }

    @SuppressWarnings("unused")
    public Stream<XsdAttributeGroup> getXsdAttributeGroup() {
        return ((XsdRestrictionsVisitor)visitor).getXsdAttributeGroups();
    }

    /**
     * @return The {@link XsdComplexType} from which this extension extends or null if the {@link XsdParserCore} wasn't
     * able to replace the {@link UnsolvedReference} created by the base attribute value.
     */
    public XsdComplexType getBaseAsComplexType() {
        if (base instanceof NamedConcreteElement){
            XsdAbstractElement baseType = base.getElement();

            if (baseType instanceof XsdComplexType){
                return (XsdComplexType) baseType;
            }
        }

        return null;
    }

    /**
     * @return The {@link XsdSimpleType} from which this extension extends or null if the {@link XsdParserCore} wasn't
     * able to replace the {@link UnsolvedReference} created by the base attribute value.
     */
    @SuppressWarnings("unused")
    public XsdSimpleType getBaseAsSimpleType() {
        if (base instanceof NamedConcreteElement){
            XsdAbstractElement baseType = base.getElement();

            if (baseType instanceof XsdSimpleType){
                return (XsdSimpleType) baseType;
            }
        }

        return null;
    }

    /**
     * @return The {@link XsdBuiltInDataType} from which this extension extends.
     */
    @SuppressWarnings("unused")
    public XsdBuiltInDataType getBaseAsBuiltInDataType() {
        if (base instanceof NamedConcreteElement){
            XsdAbstractElement baseType = base.getElement();

            if (baseType instanceof XsdBuiltInDataType){
                return (XsdBuiltInDataType) baseType;
            }
        }

        return null;
    }

    public String getBase() {
        return baseString;
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

    /**
     * @return Returns the first pattern, if available.
     */
    public XsdPattern getPattern() {
        if (pattern != null){
            Optional<XsdPattern> optionalXsdPattern = pattern.stream().findFirst();

            if (optionalXsdPattern.isPresent()){
                return optionalXsdPattern.get();
            }
        }

        return null;
    }

    /**
     * @return Returns the list of XsdPatterns.
     */
    public List<XsdPattern> getPatterns() {
        return pattern;
    }

    public void setPattern(List<XsdPattern> pattern) {
        this.pattern = pattern;
    }

    public void add(XsdPattern patternMember) {
        pattern.add(patternMember);
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

    public XsdGroup getGroup() {
        return group instanceof ConcreteElement ? (XsdGroup) group.getElement() : null;
    }

    public void setGroup(ReferenceBase group) {
        this.group = group;
    }

    public XsdAll getAll() {
        return all;
    }

    public void setAll(XsdAll all) {
        this.all = all;
    }

    public XsdChoice getChoice() {
        return choice;
    }

    public void setChoice(XsdChoice choice) {
        this.choice = choice;
    }

    public XsdSequence getSequence() {
        return sequence;
    }

    public void setSequence(XsdSequence sequence) {
        this.sequence = sequence;
    }
}
