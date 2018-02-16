package XsdElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import XsdElements.XsdRestrictionElements.*;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XsdRestriction extends XsdAbstractElement {

    public static final String XSD_TAG = "xsd:restriction";
    public static final String XS_TAG = "xs:restriction";

    private RestrictionVisitor visitor = new RestrictionVisitor();

    private List<XsdAttributeGroup> attributeGroups = new ArrayList<>();
    private List<ReferenceBase> attributes = new ArrayList<>();

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

    private XsdRestriction(XsdAbstractElement parent, HashMap<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdRestriction(HashMap<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    public void setFields(HashMap<String, String> elementFieldsMap){
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.base = elementFieldsMap.getOrDefault(BASE, base);
        }
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        this.setParent(visitor.getOwner());
    }

    @Override
    public XsdRestriction clone(HashMap<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdRestriction elementCopy = new XsdRestriction(this.getParent(), placeHolderAttributes);

        HashMap<String, String> dummy = new HashMap<>();

        elementCopy.simpleType = this.simpleType;

        elementCopy.attributes = this.attributes;
        elementCopy.attributeGroups = this.attributeGroups;
        elementCopy.enumeration = this.enumeration;

        elementCopy.fractionDigits = fractionDigits == null ? null : fractionDigits.clone(dummy);
        elementCopy.length = length == null ? null : length.clone(dummy);
        elementCopy.maxExclusive = maxExclusive == null ? null : maxExclusive.clone(dummy);
        elementCopy.maxInclusive = maxInclusive == null ? null : maxInclusive.clone(dummy);
        elementCopy.maxLength = maxLength == null ? null : maxLength.clone(dummy);
        elementCopy.minExclusive = minExclusive == null ? null : minExclusive.clone(dummy);
        elementCopy.minInclusive = minInclusive == null ? null : minInclusive.clone(dummy);
        elementCopy.minLength = minLength == null ? null : minLength.clone(dummy);
        elementCopy.pattern = pattern == null ? null : pattern.clone(dummy);
        elementCopy.totalDigits = totalDigits == null ? null : totalDigits.clone(dummy);
        elementCopy.whiteSpace = whiteSpace == null ? null : whiteSpace.clone(dummy);

        return elementCopy;
    }

    private void addAttribute(ReferenceBase attribute) {
        this.attributes.add(attribute);
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return null;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdRestriction(convertNodeMap(node.getAttributes())));
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

    class RestrictionVisitor extends Visitor{

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
    }
}
