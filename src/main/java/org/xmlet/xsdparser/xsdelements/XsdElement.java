package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.enums.BlockEnum;
import org.xmlet.xsdparser.xsdelements.enums.FinalEnum;
import org.xmlet.xsdparser.xsdelements.enums.FormEnum;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * A class representing the xsd:element element. Extends {@link XsdNamedElements} because it's one of the
 * {@link XsdAbstractElement} concrete classes that can have a {@link XsdNamedElements#name} attribute.
 *
 * @see <a href="https://www.w3schools.com/xml/el_element.asp">xsd:element description and usage at w3c</a>
 */
public class XsdElement extends XsdNamedElements {

    public static final String XSD_TAG = "xsd:element";
    public static final String XS_TAG = "xs:element";

    /**
     * {@link XsdElementVisitor} which restricts its children to {@link XsdComplexType} and {@link XsdSimpleType}
     * instances.
     * Can also have {@link XsdAnnotation} as children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
     */
    private XsdElementVisitor visitor = new XsdElementVisitor(this);

    /**
     * The {@link XsdComplexType} instance wrapped in a {@link ReferenceBase} object.
     */
    private ReferenceBase complexType;

    /**
     * The {@link XsdSimpleType} instance wrapped in a {@link ReferenceBase} object.
     */
    private ReferenceBase simpleType;

    /**
     * The type of the current element.
     * Either specified a built in data type or is a reference to a existent {@link XsdComplexType} or a
     * {@link XsdSimpleType} instances.
     */
    private ReferenceBase type;

    /**
     * Specifies the name of an element that can be substituted with this element. Only should be present if this
     * {@link XsdElement} is a top level element, i.e. his parent is a XsdSchema element.
     */
    private String substitutionGroup;

    /**
     * Specifies a default value for the element. It's only available if the type contents are text only type defined by
     * a simpleType.
     */
    private String defaultObj;

    /**
     * Specifies a fixed value for the element. It's only available if the type contents are text only type defined by
     * a simpleType.
     */
    private String fixed;

    /**
     * Specifies if the current {@link XsdElement} attribute is "qualified" or "unqualified".
     */
    private FormEnum form;

    /**
     * Specifies if the this {@link XsdElement} support a null value.
     */
    private boolean nillable;

    /**
     * Specifies whether the element can be used in an instance document.
     */
    private boolean abstractObj;

    /**
     * Prevents an element with a specified type of derivation from being used in place of this {@link XsdElement} element.
     * Possible values are:
        * extension - prevents elements derived by extension;
        * restriction - prevents elements derived by restriction;
        * substitution - prevents elements derived by substitution;
        * #all - all of the above.
     */
    private BlockEnum block;

    /**
     * Prevents other elements to derive depending on its value. This attribute cannot be present unless this
     * {@link XsdElement} is a top level element, i.e. his parent is a XsdSchema element.
         * extension - prevents elements derived by extension;
         * restriction - prevents elements derived by restriction;
         * #all - all of the above.
     */
    private FinalEnum finalObj;

    /**
     * Specifies the minimum number of times this element can occur in the parent element. The value can be any
     * number bigger or equal to 0. Default value is 1. This attribute cannot be used if the parent element is the
     * XsdSchema element.
     */
    private Integer minOccurs;

    /**
     * Specifies the maximum number of times this element can occur in the parent element. The value can be any
     * number bigger or equal to 0, or if you want to set no limit on the maximum number, use the value "unbounded".
     * Default value is 1. This attribute cannot be used if the parent element is the XsdSchema element.
     */
    private String maxOccurs;

    public XsdElement(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap) {
        super(parser, attributesMap);

        String typeString = attributesMap.get(TYPE_TAG);

        if (typeString != null){
            if (XsdParserCore.getXsdTypesToJava().containsKey(typeString)){
                HashMap<String, String> attributes = new HashMap<>();
                attributes.put(NAME_TAG, typeString);
                this.type = ReferenceBase.createFromXsd(new XsdComplexType(this, this.parser, attributes));
            } else {
                this.type = new UnsolvedReference(typeString, new XsdElement(this, this.parser, new HashMap<>()));
                parser.addUnsolvedReference((UnsolvedReference) this.type);
            }
        }

        String formDefault = AttributeValidations.getFormDefaultValue(parent);
        String blockDefault = AttributeValidations.getBlockDefaultValue(parent);
        String finalDefault = AttributeValidations.getFinalDefaultValue(parent);

        this.substitutionGroup = attributesMap.getOrDefault(SUBSTITUTION_GROUP_TAG, substitutionGroup);
        this.defaultObj = attributesMap.getOrDefault(DEFAULT_TAG, defaultObj);
        this.fixed = attributesMap.getOrDefault(FIXED_TAG, fixed);
        this.form = AttributeValidations.belongsToEnum(FormEnum.QUALIFIED, attributesMap.getOrDefault(FORM_TAG, formDefault));
        this.nillable = AttributeValidations.validateBoolean(attributesMap.getOrDefault(NILLABLE_TAG, "false"));
        this.abstractObj = AttributeValidations.validateBoolean(attributesMap.getOrDefault(ABSTRACT_TAG, "false"));
        this.block = AttributeValidations.belongsToEnum(BlockEnum.ALL, attributesMap.getOrDefault(BLOCK_TAG, blockDefault));
        this.finalObj = AttributeValidations.belongsToEnum(FinalEnum.ALL, attributesMap.getOrDefault(FINAL_TAG, finalDefault));
        this.minOccurs = AttributeValidations.validateNonNegativeInteger(XSD_TAG, MIN_OCCURS_TAG, attributesMap.getOrDefault(MIN_OCCURS_TAG, "1"));
        this.maxOccurs = AttributeValidations.maxOccursValidation(XSD_TAG, attributesMap.getOrDefault(MAX_OCCURS_TAG, "1"));
    }

    public XsdElement(XsdAbstractElement parent, @NotNull XsdParserCore parser, @NotNull Map<String, String> elementFieldsMapParam) {
        this(parser, elementFieldsMapParam);
        setParent(parent);
    }

    /**
     * Runs verifications on each concrete element to ensure that the XSD schema rules are verified.
     */
    @Override
    public void validateSchemaRules() {
        super.validateSchemaRules();

        rule2();
        rule3();
        rule4();
        rule5();
        rule6();
        rule7();
    }

    private static String xsdElementIsXsdSchema = XSD_TAG + " is a " + XsdSchema.XSD_TAG + " element.";

    /**
     * Asserts if the current object has a form attribute while being a direct child of the top level XsdSchema element,
     * which isn't allowed, throwing an exception in that case.
     */
    private void rule7() {
        if (parent instanceof XsdSchema && attributesMap.containsKey(FORM_TAG)){
            throw new ParsingException(XSD_TAG + " element: The " + FORM_TAG + " attribute can only be present when the parent of the " + xsdElementIsXsdSchema);
        }
    }

    private void rule6() {
        //fixed 	Optional. Specifies a fixed value for the element (can only be used if the element's content is a simple type or text only)
    }

    private void rule5() {
        // default 	Optional. Specifies a default value for the element (can only be used if the element's content is a simple type or text only)
    }

    /**
     * Asserts if the current object isn't a direct child of the top level XsdSchema and has a value for the substitutionGroup,
     * which isn't allowed, throwing an exception in that case.
     */
    private void rule4() {
        if (!(parent instanceof XsdSchema) && substitutionGroup != null){
            throw new ParsingException(XSD_TAG + " element: The " + SUBSTITUTION_GROUP_TAG + " attribute can only be present when the parent of the " + xsdElementIsXsdSchema);
        }
    }

    /**
     * Asserts if the current object has a ref attribute while being a direct child of the top level XsdSchema element, which isn't allowed,
     * throwing an exception in that case.
     */
    private void rule3() {
        if (parent instanceof XsdSchema && attributesMap.containsKey(REF_TAG)){
            throw new ParsingException(XSD_TAG + " element: The " + REF_TAG + " attribute cannot be present when the parent of the " + xsdElementIsXsdSchema);
        }
    }

    /**
     * Asserts if the current object is a direct child of the top level XsdSchema element and doesn't have a name, which isn't allowed,
     * throwing an exception in that case.
     */
    private void rule2() {
        if (parent instanceof XsdSchema && name == null){
            throw new ParsingException(XSD_TAG + " element: The " + NAME_TAG + " attribute is required when the parent of the " + xsdElementIsXsdSchema);
        }
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public XsdElementVisitor getVisitor() {
        return visitor;
    }

    /**
     * Performs a copy of the current object for replacing purposes. The cloned objects are used to replace
     * {@link UnsolvedReference} objects in the reference solving process.
     * @param placeHolderAttributes The additional attributes to add to the clone.
     * @return A copy of the object from which is called upon.
     */
    @Override
    public XsdElement clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(attributesMap);
        placeHolderAttributes.remove(TYPE_TAG);
        placeHolderAttributes.remove(REF_TAG);

        XsdElement elementCopy = new XsdElement(this.parent, this.parser, placeHolderAttributes);

        elementCopy.simpleType = this.simpleType;
        elementCopy.complexType = this.complexType;
        elementCopy.type = this.type;

        return elementCopy;
    }

    /**
     * This method aims to replace the previously created {@link UnsolvedReference} in case that the type of the
     * current {@link XsdElement} instance is not a built-in type.
     * @param element A concrete element with a name that will replace the {@link UnsolvedReference} object created in the
     *                {@link XsdElement} constructor. The {@link UnsolvedReference} is only replaced if there
     *                is a match between the {@link UnsolvedReference#ref} and the {@link NamedConcreteElement#name}.
     */
    @Override
    public void replaceUnsolvedElements(NamedConcreteElement element) {
        super.replaceUnsolvedElements(element);

        XsdNamedElements elem = element.getElement();

        boolean isComplexOrSimpleType = elem instanceof XsdComplexType || elem instanceof XsdSimpleType;

        if (this.type instanceof UnsolvedReference && isComplexOrSimpleType && ((UnsolvedReference) this.type).getRef().equals(element.getName())){
            this.type = element;
            elem.setParent(this);
        }
    }

    public XsdComplexType getXsdComplexType() {
        return complexType == null ? getXsdType() : (XsdComplexType) complexType.getElement();
    }

    public XsdSimpleType getXsdSimpleType(){
        return simpleType instanceof ConcreteElement ? (XsdSimpleType) simpleType.getElement() : null;
    }

    private XsdComplexType getXsdType(){
        if (type instanceof ConcreteElement){
            return (XsdComplexType) type.getElement();
        }

        return null;
    }

    public static ReferenceBase parse(@NotNull XsdParserCore parser, Node node){
        return xsdParseSkeleton(node, new XsdElement(parser, convertNodeMap(node.getAttributes())));
    }

    public String getFinal() {
        return finalObj.getValue();
    }

    @SuppressWarnings("unused")
    public boolean isNillable() {
        return nillable;
    }

    @SuppressWarnings("unused")
    public Integer getMinOccurs() {
        return minOccurs;
    }

    @SuppressWarnings("unused")
    public String getMaxOccurs() {
        return maxOccurs;
    }

    @SuppressWarnings("unused")
    public boolean isAbstractObj() {
        return abstractObj;
    }

    public void setComplexType(ReferenceBase complexType) {
        this.complexType = complexType;
    }

    public void setSimpleType(ReferenceBase simpleType) {
        this.simpleType = simpleType;
    }

    @SuppressWarnings("unused")
    public String getBlock() {
        return block.getValue();
    }

    @SuppressWarnings("unused")
    public String getForm() {
        return form.getValue();
    }

    public String getType(){
        return attributesMap.getOrDefault(TYPE_TAG, null);
    }
}
