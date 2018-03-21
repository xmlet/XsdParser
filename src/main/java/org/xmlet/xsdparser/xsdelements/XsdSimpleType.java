package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

import javax.validation.constraints.NotNull;
import java.security.InvalidParameterException;
import java.util.*;

import static org.xmlet.xsdparser.xsdelements.xsdrestrictions.XsdIntegerRestrictions.hasDifferentValue;
import static org.xmlet.xsdparser.xsdelements.xsdrestrictions.XsdStringRestrictions.hasDifferentValue;

public class XsdSimpleType extends XsdReferenceElement {

    public static final String XSD_TAG = "xsd:simpleType";
    public static final String XS_TAG = "xs:simpleType";

    private SimpleTypeXsdElementVisitor visitor = new SimpleTypeXsdElementVisitor();

    private XsdRestriction restriction;
    private XsdUnion union;
    private XsdList list;

    private String finalObj;

    private XsdSimpleType(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam){
        super.setFields(elementFieldsMapParam);

        this.finalObj = elementFieldsMap.getOrDefault(FINAL_TAG, finalObj);
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
    public XsdSimpleType clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(elementFieldsMap);
        placeHolderAttributes.remove(REF_TAG);

        XsdSimpleType copy = new XsdSimpleType(placeHolderAttributes);
        copy.setParent(this.parent);

        copy.union = this.union;
        copy.list = this.list;
        copy.restriction = this.restriction;

        return copy;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdSimpleType(convertNodeMap(node.getAttributes())));
    }

    public XsdRestriction getRestriction() {
        return restriction;
    }

    public XsdUnion getUnion() {
        return union;
    }

    public XsdList getList() {
        if (this.list == null && this.union != null){
            Optional<XsdSimpleType> simpleType = union.getUnionElements().stream().filter(xsdSimpleType -> xsdSimpleType.list != null).findFirst();

            if (simpleType.isPresent()){
                return simpleType.get().list;
            }
        }

        return this.list;
    }

    /**
     * This method obtains all the restrictions for the current XsdSimpleType element.
     * It also joins multiple restrictions with the same base attribute in the same XsdRestriction
     * object, if a overlap doesn't occur. In case of restriction overlap an exception is thrown
     * because the information on the xsd file is contradictory.
     * @return A list of restrictions.
     */
    public List<XsdRestriction> getAllRestrictions() {
        Map<String, XsdRestriction> restrictions = new HashMap<>();
        Map<String, String> xsdBuiltinTypes = XsdParser.getXsdTypesToJava();

        if (restriction != null){
            restrictions.put(xsdBuiltinTypes.get(restriction.getBase()), restriction);
        }

        if (union != null){
            union.getUnionElements().forEach(unionMember -> {
                XsdRestriction unionMemberRestriction = unionMember.getRestriction();

                if (unionMemberRestriction != null){
                    XsdRestriction existingRestriction = restrictions.getOrDefault(xsdBuiltinTypes.get(unionMemberRestriction.getBase()), null);

                    if (existingRestriction != null){
                        if (existsRestrictionOverlap(existingRestriction, unionMemberRestriction)){
                            throw new InvalidParameterException("The xsd file is invalid because has contradictory restrictions.");
                        }

                        updateExistingRestriction(existingRestriction, unionMemberRestriction);
                    } else {
                        restrictions.put(xsdBuiltinTypes.get(unionMemberRestriction.getBase()), unionMemberRestriction);
                    }
                }
            });
        }

        return new ArrayList<>(restrictions.values());
    }

    private void updateExistingRestriction(XsdRestriction existing, XsdRestriction newRestriction) {
        XsdPattern pattern = newRestriction.getPattern();
        XsdMaxExclusive maxExclusive = newRestriction.getMaxExclusive();
        XsdMaxInclusive maxInclusive = newRestriction.getMaxInclusive();
        XsdMaxLength maxLength = newRestriction.getMaxLength();
        XsdMinExclusive minExclusive = newRestriction.getMinExclusive();
        XsdMinInclusive minInclusive = newRestriction.getMinInclusive();
        XsdMinLength minLength = newRestriction.getMinLength();
        XsdLength length = newRestriction.getLength();
        XsdFractionDigits fractionDigits = newRestriction.getFractionDigits();
        XsdTotalDigits totalDigits = newRestriction.getTotalDigits();
        XsdWhiteSpace whiteSpace = newRestriction.getWhiteSpace();

        if (pattern != null){
            existing.setPattern(pattern);
        }

        if (maxExclusive != null){
            existing.setMaxExclusive(maxExclusive);
        }

        if (maxInclusive != null){
            existing.setMaxInclusive(maxInclusive);
        }

        if (maxLength != null){
            existing.setMaxLength(maxLength);
        }

        if (minExclusive != null){
            existing.setMinExclusive(minExclusive);
        }

        if (minInclusive != null){
            existing.setMinInclusive(minInclusive);
        }

        if (minLength != null){
            existing.setMinLength(minLength);
        }

        if (length != null){
            existing.setLength(length);
        }

        if (fractionDigits != null){
            existing.setFractionDigits(fractionDigits);
        }

        if (totalDigits != null){
            existing.setTotalDigits(totalDigits);
        }

        if (whiteSpace != null){
            existing.setWhiteSpace(whiteSpace);
        }

        updateExistingRestrictionEnumerations(existing);
    }

    private void updateExistingRestrictionEnumerations(XsdRestriction existing) {
        List<XsdEnumeration> existingEnumeration = existing.getEnumeration();
        List<XsdEnumeration> newRestrictionEnumeration = existing.getEnumeration();

        if (existingEnumeration == null){
            existing.setEnumeration(newRestrictionEnumeration);
        } else {
            if (newRestrictionEnumeration != null){
                for (XsdEnumeration enumerationElem : newRestrictionEnumeration){
                    if (existingEnumeration.stream().noneMatch(existingEnumerationElem -> existingEnumerationElem.getValue().equals(enumerationElem.getValue()))){
                        existingEnumeration.add(enumerationElem);
                    }
                }
            }
        }
    }

    /**
     * Checks for any restriction overlap between two different XsdRestriction objects.
     * @param existing The existing restriction.
     * @param newRestriction The second restriction found.
     * @return True if an overlap between the restrictions occur, false if it doesn't ocurr.
     */
    private boolean existsRestrictionOverlap(XsdRestriction existing, XsdRestriction newRestriction) {
        return hasDifferentValue(existing.getPattern(), newRestriction.getPattern()) ||
               hasDifferentValue(existing.getWhiteSpace(), newRestriction.getWhiteSpace()) ||
               hasDifferentValue(existing.getTotalDigits(), newRestriction.getTotalDigits()) ||
               hasDifferentValue(existing.getFractionDigits(), newRestriction.getFractionDigits()) ||
               hasDifferentValue(existing.getMaxExclusive(), newRestriction.getMaxExclusive()) ||
               hasDifferentValue(existing.getMaxInclusive(), newRestriction.getMaxInclusive()) ||
               hasDifferentValue(existing.getMaxLength(), newRestriction.getMaxLength()) ||
               hasDifferentValue(existing.getMinExclusive(), newRestriction.getMinExclusive()) ||
               hasDifferentValue(existing.getMinInclusive(), newRestriction.getMinInclusive()) ||
               hasDifferentValue(existing.getMinLength(), newRestriction.getMinLength()) ||
               hasDifferentValue(existing.getLength(), newRestriction.getLength());
    }

    class SimpleTypeXsdElementVisitor extends AnnotatedXsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdSimpleType.this;
        }

        @Override
        public void visit(XsdList element) {
            super.visit(element);

            XsdSimpleType.this.list = element;
        }

        @Override
        public void visit(XsdUnion element) {
            super.visit(element);

            XsdSimpleType.this.union = element;
        }

        @Override
        public void visit(XsdRestriction element) {
            super.visit(element);

            XsdSimpleType.this.restriction = element;
        }
    }
}
