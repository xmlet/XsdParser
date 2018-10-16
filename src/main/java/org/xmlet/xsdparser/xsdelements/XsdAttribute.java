package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.enums.FormEnum;
import org.xmlet.xsdparser.xsdelements.enums.UsageEnum;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAttributeVisitor;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class representing the xsd:attribute element. It can have a ref attribute and therefore extends from
 * {@link XsdNamedElements}, which serves as a base to every element type that can have a ref attribute.
 * For more information check {@link XsdNamedElements}.
 *
 * @see <a href="https://www.w3schools.com/xml/el_attribute.asp">xsd:attribute element description and usage at w3c</a>
 */
public class XsdAttribute extends XsdNamedElements {

    public static final String XSD_TAG = "xsd:attribute";
    public static final String XS_TAG = "xs:attribute";

    /**
     * {@link XsdAttributeVisitor} instance which restricts the its children to {@link XsdSimpleType}.
     * Can also have xsd:annotation as children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
     */
    private XsdAttributeVisitor visitor = new XsdAttributeVisitor(this);

    /**
     * A {@link XsdSimpleType} instance wrapped in a {@link ReferenceBase} object which indicate any restrictions
     * that may be present in the current {@link XsdAttribute} instance.
     */
    private ReferenceBase simpleType;

    /**
     * A default value for the current {@link XsdAttribute} instance. This value and {@link XsdAttribute#fixed}
     * shouldn't be present at the same time.
     */
    private String defaultElement;

    /**
     * Specifies a fixed value for the current {@link XsdAttribute} instance. This value and
     * {@link XsdAttribute#defaultElement} shouldn't be present at the same time.
     */
    private String fixed;

    /**
     * Specifies either a built-in data type for the current {@link XsdAttribute} instance or serves as a reference to a
     * {@link XsdSimpleType} instance. In the case of being used as a reference to a {@link XsdSimpleType} instance
     * its value is used to create an {@link UnsolvedReference} using its value as ref to be resolved later in the
     * parsing process.
     */
    private String type;

    /**
     * Specifies if the current {@link XsdAttribute} attribute is "qualified" or "unqualified".
     */
    private FormEnum form;

    /**
     * Specifies how this {@link XsdAttribute} should be used. The possible values are: required, prohibited, optional.
     */
    private UsageEnum use;

    private XsdAttribute(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    private XsdAttribute(XsdAbstractElement parent, @NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
        setParent(parent);
    }

    /**
     * Sets all the fields of the {@link XsdAttribute} instance. Most values don't have default values except the
     * {@link XsdAttribute#use} field. Regarding the {@link XsdAttribute#type} field we check if the value present is a
     * built-in type, if not we create an {@link UnsolvedReference} object to be resolved at a later time in the
     * parsing process.
     * @param elementFieldsMapParam The Map object containing all the information previously contained in the parsed Node.
     */
    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        String formDefaultValue = getFormDefaultValue(parent);

        this.defaultElement = elementFieldsMap.getOrDefault(DEFAULT_ELEMENT_TAG, defaultElement);
        this.fixed = elementFieldsMap.getOrDefault(FIXED_TAG, fixed);
        this.type = elementFieldsMap.getOrDefault(TYPE_TAG, type);
        this.form = AttributeValidations.belongsToEnum(FormEnum.QUALIFIED, elementFieldsMap.getOrDefault(FORM_TAG, formDefaultValue));
        this.use = AttributeValidations.belongsToEnum(UsageEnum.OPTIONAL, elementFieldsMap.getOrDefault(USE_TAG, UsageEnum.OPTIONAL.getValue()));

        if (type != null && !XsdParser.getXsdTypesToJava().containsKey(type)){
            this.simpleType = new UnsolvedReference(type, new XsdAttribute(this, parser, new HashMap<>()));
            parser.addUnsolvedReference((UnsolvedReference) this.simpleType);
        }
    }

    private static String getFormDefaultValue(XsdAbstractElement parent) {
        if (parent == null) return null;

        if (parent instanceof XsdElement){
            return ((XsdElement) parent).getForm();
        }

        if (parent instanceof XsdSchema){
            return ((XsdSchema) parent).getAttributeFormDefault();
        }

        return getFormDefaultValue(parent.getParent());
    }

    /**
     * Runs verifications on each concrete element to ensure that the XSD schema rules are verified.
     */
    @Override
    public void validateSchemaRules() {
        super.validateSchemaRules();

        rule2();
        rule3();
    }

    /**
     * Asserts if the current object has a ref attribute at the same time as either a simpleType as children, a form attribute or a type attribute.
     * Throws an exception in that case.
     */
    private void rule3() {
        if (elementFieldsMap.containsKey(REF_TAG) && (simpleType != null || form != null || type != null)){
            throw new ParsingException(XSD_TAG + " element: If " + REF_TAG + " attribute is present, simpleType element, form attribute and type attribute cannot be present at the same time.");
        }
    }

    /**
     * Asserts if the current object has the fixed and default attributes at the same time, which isn't allowed, throwing
     * an exception in that case.
     */
    private void rule2() {
        if (fixed != null && defaultElement != null){
            throw new ParsingException(XSD_TAG + " element: " + FIXED_TAG + " and " + DEFAULT_ELEMENT_TAG + " attributes are not allowed at the same time.");
        }
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public XsdAttributeVisitor getVisitor() {
        return visitor;
    }

    /**
     * Performs a copy of the current object for replacing purposes. The cloned objects are used to replace
     * {@link UnsolvedReference} objects in the reference solving process.
     * @param placeHolderAttributes The additional attributes to add to the clone.
     * @return A copy of the object from which is called upon.
     */
    @Override
    public XsdAttribute clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(elementFieldsMap);
        placeHolderAttributes.remove(TYPE_TAG);
        placeHolderAttributes.remove(REF_TAG);

        XsdAttribute copy = new XsdAttribute(this.parent, this.parser, placeHolderAttributes);

        copy.type = this.type;
        copy.simpleType = this.simpleType;

        return copy;
    }

    /**
     * Receives a {@link NamedConcreteElement} that should be the one requested earlier.
     *  * In the {@link XsdAttribute#setFields} method:
     *      this.simpleType = new UnsolvedReference(type, placeHolder);
     *      XsdParser.getInstance().addUnsolvedReference((UnsolvedReference) this.simpleType);
     * This implies that the object being received is the object that is being referred with the {@link XsdAttribute#type}
     * String.
     * @param elementWrapper The object that should be wrapping the requested {@link XsdSimpleType} object.
     */
    @Override
    public void replaceUnsolvedElements(NamedConcreteElement elementWrapper) {
        super.replaceUnsolvedElements(elementWrapper);

        XsdAbstractElement element = elementWrapper.getElement();

        if (element instanceof XsdSimpleType && simpleType != null && type.equals(elementWrapper.getName())){
            this.simpleType = elementWrapper;
        }
    }

    public void setSimpleType(ReferenceBase simpleType) {
        this.simpleType = simpleType;
    }

    public XsdSimpleType getXsdSimpleType(){
        return simpleType instanceof ConcreteElement ? (XsdSimpleType) simpleType.getElement() : null;
    }

    public String getType() {
        return type;
    }

    public String getUse() {
        return use.getValue();
    }

    public String getForm() {
        return form.getValue();
    }

    public String getFixed() {
        return fixed;
    }

    @SuppressWarnings("unused")
    public List<XsdRestriction> getAllRestrictions(){
        XsdSimpleType simpleTypeObj = getXsdSimpleType();

        if (simpleTypeObj != null){
            return simpleTypeObj.getAllRestrictions();
        }

        return Collections.emptyList();
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node) {
        return xsdParseSkeleton(node, new XsdAttribute(parser, convertNodeMap(node.getAttributes())));
    }

}
