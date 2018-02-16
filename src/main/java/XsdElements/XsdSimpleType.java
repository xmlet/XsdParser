package XsdElements;

//TODO XsdLists tem um built in type ou outro ?simple type?, que já está resolvido e devem poder ter XsdRestriction lá dentro.

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import XsdElements.XsdRestrictionElements.XsdEnumeration;
import XsdParser.XsdParser;
import org.w3c.dom.Node;

import java.util.*;

public class XsdSimpleType extends XsdAbstractElement {

    public static final String XSD_TAG = "xsd:simpleType";
    public static final String XS_TAG = "xs:simpleType";

    private SimpleTypeVisitor visitor = new SimpleTypeVisitor();

    private XsdRestriction restriction;
    private XsdUnion union;
    private XsdList list;

    private String name;
    private String finalObj;

    private XsdSimpleType(XsdAbstractElement parent, HashMap<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdSimpleType(HashMap<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    public void setFields(HashMap<String, String> elementFieldsMap){
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.name = elementFieldsMap.getOrDefault(NAME, name);
            this.finalObj = elementFieldsMap.getOrDefault(FINAL, finalObj);
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
    public XsdSimpleType clone(HashMap<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdSimpleType copy = new XsdSimpleType(this.getParent(), placeHolderAttributes);

        copy.union = this.union;
        copy.list = this.list;
        copy.restriction = this.restriction;

        return copy;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return null;
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
        if (this.list == null){
            if (union != null){
                Optional<XsdSimpleType> simpleType = union.getUnionElements().stream().filter(xsdSimpleType -> xsdSimpleType.list != null).findFirst();

                if (simpleType.isPresent()){
                    return simpleType.get().list;
                }
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
    List<XsdRestriction> getAllRestrictions() {
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
                            throw new RuntimeException("The xsd file is invalid because has contradictory restrictions.");
                        }

                        existingRestriction.setPattern(unionMemberRestriction.getPattern() == null ? existingRestriction.getPattern() : unionMemberRestriction.getPattern());
                        existingRestriction.setMaxExclusive(unionMemberRestriction.getMaxExclusive() == null ? existingRestriction.getMaxExclusive() : unionMemberRestriction.getMaxExclusive());
                        existingRestriction.setMaxInclusive(unionMemberRestriction.getMaxInclusive() == null ? existingRestriction.getMaxInclusive() : unionMemberRestriction.getMaxInclusive());
                        existingRestriction.setMaxLength(unionMemberRestriction.getMaxLength() == null ? existingRestriction.getMaxLength() : unionMemberRestriction.getMaxLength());
                        existingRestriction.setMinExclusive(unionMemberRestriction.getMinExclusive() == null ? existingRestriction.getMinExclusive() : unionMemberRestriction.getMinExclusive());
                        existingRestriction.setMinInclusive(unionMemberRestriction.getMinInclusive() == null ? existingRestriction.getMinInclusive() : unionMemberRestriction.getMinInclusive());
                        existingRestriction.setMinLength(unionMemberRestriction.getMinLength() == null ? existingRestriction.getMinLength() : unionMemberRestriction.getMinLength());
                        existingRestriction.setLength(unionMemberRestriction.getLength() == null ? existingRestriction.getLength() : unionMemberRestriction.getLength());
                        existingRestriction.setFractionDigits(unionMemberRestriction.getFractionDigits() == null ? existingRestriction.getFractionDigits() : unionMemberRestriction.getFractionDigits());
                        existingRestriction.setTotalDigits(unionMemberRestriction.getTotalDigits() == null ? existingRestriction.getTotalDigits() : unionMemberRestriction.getTotalDigits());
                        existingRestriction.setWhiteSpace(unionMemberRestriction.getWhiteSpace() == null ? existingRestriction.getWhiteSpace() : unionMemberRestriction.getWhiteSpace());

                        List<XsdEnumeration> existingEnumeration = existingRestriction.getEnumeration();
                        List<XsdEnumeration> newRestrictionEnumeration = existingRestriction.getEnumeration();

                        if (existingEnumeration == null){
                            existingRestriction.setEnumeration(newRestrictionEnumeration);
                        } else {
                            if (newRestrictionEnumeration != null){
                                for (XsdEnumeration enumerationElem : newRestrictionEnumeration){
                                    if (existingEnumeration.stream().noneMatch(existingEnumerationElem -> existingEnumerationElem.getValue().equals(enumerationElem.getValue()))){
                                        existingEnumeration.add(enumerationElem);
                                    }
                                }
                            }
                        }
                    } else {
                        restrictions.put(xsdBuiltinTypes.get(unionMemberRestriction.getBase()), unionMemberRestriction);
                    }
                }
            });
        }

        return new ArrayList<>(restrictions.values());
    }

    /**
     * Checks for any restriction overlap between two different XsdRestriction objects.
     * @param existingRestriction The existing restriction.
     * @param unionMemberRestriction The second restriction found.
     * @return True if an overlap between the restrictions occur, false if it doesn't ocurr.
     */
    private boolean existsRestrictionOverlap(XsdRestriction existingRestriction, XsdRestriction unionMemberRestriction) {
        return  (existingRestriction.getWhiteSpace() != null && unionMemberRestriction.getWhiteSpace() != null && existingRestriction.getWhiteSpace() != unionMemberRestriction.getWhiteSpace()) ||
                (existingRestriction.getPattern() != null && unionMemberRestriction.getPattern() != null && existingRestriction.getPattern() != unionMemberRestriction.getPattern()) ||
                (existingRestriction.getTotalDigits() != null && unionMemberRestriction.getTotalDigits() != null && existingRestriction.getTotalDigits().getValue() != unionMemberRestriction.getTotalDigits().getValue()) ||
                (existingRestriction.getFractionDigits() != null && unionMemberRestriction.getFractionDigits() != null && existingRestriction.getFractionDigits().getValue() != unionMemberRestriction.getFractionDigits().getValue()) ||
                (existingRestriction.getMaxExclusive() != null && unionMemberRestriction.getMaxExclusive() != null && existingRestriction.getMaxExclusive().getValue() != unionMemberRestriction.getMaxExclusive().getValue()) ||
                (existingRestriction.getMaxInclusive() != null && unionMemberRestriction.getMaxInclusive() != null && existingRestriction.getMaxInclusive().getValue() != unionMemberRestriction.getMaxInclusive().getValue()) ||
                (existingRestriction.getMaxLength() != null && unionMemberRestriction.getMaxLength() != null && existingRestriction.getMaxLength().getValue() != unionMemberRestriction.getMaxLength().getValue()) ||
                (existingRestriction.getMinExclusive() != null && unionMemberRestriction.getMinExclusive() != null && existingRestriction.getMinExclusive().getValue() != unionMemberRestriction.getMinExclusive().getValue()) ||
                (existingRestriction.getMinInclusive() != null && unionMemberRestriction.getMinInclusive() != null && existingRestriction.getMinInclusive().getValue() != unionMemberRestriction.getMinInclusive().getValue()) ||
                (existingRestriction.getMinLength() != null && unionMemberRestriction.getMinLength() != null && existingRestriction.getMinLength().getValue() != unionMemberRestriction.getMinLength().getValue()) ||
                (existingRestriction.getLength() != null && unionMemberRestriction.getLength() != null && existingRestriction.getLength().getValue() != unionMemberRestriction.getLength().getValue());
    }

    class SimpleTypeVisitor extends Visitor{

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
