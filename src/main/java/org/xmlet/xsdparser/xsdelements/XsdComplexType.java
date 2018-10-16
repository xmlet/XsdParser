package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.enums.ComplexTypeBlockEnum;
import org.xmlet.xsdparser.xsdelements.enums.FinalEnum;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;
import org.xmlet.xsdparser.xsdelements.visitors.AttributesVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdComplexTypeVisitor;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A class representing the xsd:complexType element. Extends {@link XsdNamedElements} because it's one of the
 * {@link XsdAbstractElement} concrete classes that can have a name attribute.
 *
 * @see <a href="https://www.w3schools.com/xml/el_complextype.asp">xsd:complexType element description and usage at w3c</a>
 */
public class XsdComplexType extends XsdNamedElements {

    public static final String XSD_TAG = "xsd:complexType";
    public static final String XS_TAG = "xs:complexType";

    /**
     * {@link XsdComplexTypeVisitor} instance which restricts the children elements to:
     *      * {@link XsdAll}, {@link XsdSequence} and {@link XsdChoice} (represented by their base class
     *          {@link XsdMultipleElements});
     *      * {@link XsdGroup};
     *      * {@link XsdComplexContent};
     *      * {@link XsdSimpleContent};
     * Can also have {@link XsdAttribute} and {@link XsdAttributeGroup} children as per inheritance of
     *      {@link AttributesVisitor}.
     * Can also have {@link XsdAnnotation} children as per inheritance of {@link XsdAnnotatedElementsVisitor }.
     */
    private XsdComplexTypeVisitor visitor = new XsdComplexTypeVisitor(this);

    /**
     * The child element of {@link XsdComplexType}. Can be either a {@link XsdGroup} or a {@link XsdMultipleElements}
     * instance wrapped in a {@link ReferenceBase} object.
     */
    private ReferenceBase childElement;

    /**
     * Specifies whether the complex type can be used in an instance document. True indicates that an element cannot
     * use this complex type directly but must use a complex type derived from this complex type.
     */
    private boolean elementAbstract;

    /**
     * Specifies whether character data is allowed to appear between the child elements of this complexType element.
     * This attribute is exclusive with {@link XsdComplexType#simpleContent}, only one can be present at any given time.
     */
    private boolean mixed;

    /**
     * Prevents a complex type that has a specified type of derivation from being used in place of this complex type.
     * Possible values are extension, restriction or #all.
     */
    private ComplexTypeBlockEnum block;

    /**
     * Prevents a specified type of derivation of this complex type element.
     * Possible values are extension, restriction or #all.
     */
    private FinalEnum elementFinal;

    /**
     * A {@link XsdComplexContent} child.
     */
    private XsdComplexContent complexContent;

    /**
     * A {@link XsdSimpleContent} child. This element is exclusive with the {@link XsdComplexType#mixed} field, only one
     * of them should be present in any {@link XsdComplexType} element.
     */
    private XsdSimpleContent simpleContent;

    XsdComplexType(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    XsdComplexType(XsdAbstractElement parent, @NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
        setParent(parent);
    }

    /**
     * Extracts the field values from the received Map.
     * The {@link XsdComplexType#elementAbstract} and {@link XsdComplexType#mixed} field both have the false as a
     * default value.
     * @param elementFieldsMapParam The Map object containing the information previously contained by the Node element.
     */
    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        String blockDefault = AttributeValidations.getBlockDefaultValue(parent);
        String finalDefault = AttributeValidations.getFinalDefaultValue(parent);

        this.elementAbstract = AttributeValidations.validateBoolean(elementFieldsMap.getOrDefault(ABSTRACT_TAG, "false"));
        this.mixed = AttributeValidations.validateBoolean(elementFieldsMap.getOrDefault(MIXED_TAG, "false"));
        this.block = AttributeValidations.belongsToEnum(ComplexTypeBlockEnum.ALL, elementFieldsMap.getOrDefault(BLOCK_TAG, blockDefault));
        this.elementFinal = AttributeValidations.belongsToEnum(FinalEnum.ALL, elementFieldsMap.getOrDefault(FINAL_TAG, finalDefault));
    }

    /**
     * Runs verifications on each concrete element to ensure that the XSD schema rules are verified.
     */
    @Override
    public void validateSchemaRules(){
        super.validateSchemaRules();

        rule2();
    }

    /**
     * Asserts if the current object has a simpleContent as children and contains a value for the mixed attribute, which isn't allowed throwing
     * an exception in that case.
     */
    private void rule2() {
        if (simpleContent != null && elementFieldsMap.containsKey(MIXED_TAG)){
            throw new ParsingException(XSD_TAG + " element: The simpleContent element and the " + MIXED_TAG  + " attribute are not allowed at the same time.");
        }
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public XsdComplexTypeVisitor getVisitor() {
        return visitor;
    }

    /**
     * @return The elements of his child as if they belong to the {@link XsdComplexType} instance.
     */
    @Override
    public List<ReferenceBase> getElements() {
        return childElement == null ? null : childElement.getElement().getElements();
    }

    /**
     * Performs a copy of the current object for replacing purposes. The cloned objects are used to replace
     * {@link UnsolvedReference} objects in the reference solving process.
     * @param placeHolderAttributes The additional attributes to add to the clone.
     * @return A copy of the object from which is called upon.
     */
    @Override
    public XsdComplexType clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(elementFieldsMap);
        placeHolderAttributes.remove(REF_TAG);

        XsdComplexType elementCopy = new XsdComplexType(this.parent, this.parser, placeHolderAttributes);

        elementCopy.childElement = this.childElement;
        elementCopy.visitor.setAttributes(this.visitor.getAttributes());
        elementCopy.visitor.setAttributeGroups(this.visitor.getAttributeGroups());

        elementCopy.complexContent = this.complexContent;
        elementCopy.simpleContent = this.simpleContent;

        return elementCopy;
    }

    @Override
    public void replaceUnsolvedElements(NamedConcreteElement element) {
        super.replaceUnsolvedElements(element);
        visitor.replaceUnsolvedAttributes(element);

        if (this.childElement instanceof UnsolvedReference && this.childElement.getElement() instanceof XsdGroup &&
                element.getElement() instanceof XsdGroup && ((UnsolvedReference) this.childElement).getRef().equals(element.getName())){
            this.childElement = element;
            element.getElement().setParent(this);
        }
    }

    public XsdAbstractElement getXsdChildElement() {
        return childElement == null ? null : childElement.getElement();
    }

    public String getFinal() {
        return elementFinal.getValue();
    }

    List<ReferenceBase> getAttributes() {
        return visitor.getAttributes();
    }

    public Stream<XsdAttribute> getXsdAttributes() {
        return visitor.getXsdAttributes();
    }

    public Stream<XsdAttributeGroup> getXsdAttributeGroup() {
        return visitor.getXsdAttributeGroup();
    }

    @SuppressWarnings("unused")
    public XsdSimpleContent getSimpleContent() {
        return simpleContent;
    }

    public XsdComplexContent getComplexContent() {
        return complexContent;
    }

    @SuppressWarnings("unused")
    public boolean isMixed() {
        return mixed;
    }

    @SuppressWarnings("unused")
    public boolean isElementAbstract() {
        return elementAbstract;
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node){
        return xsdParseSkeleton(node, new XsdComplexType(parser, convertNodeMap(node.getAttributes())));
    }

    public void setChildElement(ReferenceBase childElement) {
        this.childElement = childElement;
    }

    public void setComplexContent(XsdComplexContent complexContent) {
        this.complexContent = complexContent;
    }

    public void setSimpleContent(XsdSimpleContent simpleContent) {
        this.simpleContent = simpleContent;
    }

    @SuppressWarnings("unused")
    public String getBlock() {
        return block.getValue();
    }

    /**
     * @return The childElement as a {@link XsdGroup} object or null if childElement isn't a {@link XsdGroup} instance.
     */
    @SuppressWarnings("unused")
    public XsdGroup getChildAsGroup() {
        return childElement.getElement() instanceof XsdGroup ? (XsdGroup) childElement.getElement() : null;
    }

    /**
     * @return The childElement as a {@link XsdAll} object or null if childElement isn't a {@link XsdAll} instance.
     */
    @SuppressWarnings("unused")
    public XsdAll getChildAsAll() {
        return childrenIsMultipleElement() ? XsdMultipleElements.getChildAsdAll((XsdMultipleElements) childElement.getElement()) : null;
    }

    /**
     * @return The childElement as a {@link XsdChoice} object or null if childElement isn't a {@link XsdChoice} instance.
     */
    @SuppressWarnings("unused")
    public XsdChoice getChildAsChoice() {
        return childrenIsMultipleElement() ? XsdMultipleElements.getChildAsChoice((XsdMultipleElements) childElement.getElement()) : null;
    }

    /**
     * @return The childElement as a {@link XsdSequence} object or null if childElement isn't a {@link XsdSequence} instance.
     */
    @SuppressWarnings("unused")
    public XsdSequence getChildAsSequence() {
        return childrenIsMultipleElement() ? XsdMultipleElements.getChildAsSequence((XsdMultipleElements) childElement.getElement()) : null;
    }

    private boolean childrenIsMultipleElement(){
        return childElement.getElement() instanceof XsdMultipleElements;
    }
}
