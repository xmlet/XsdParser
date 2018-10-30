package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdGroupVisitor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A class representing the xsd:complexType element. Extends {@link XsdNamedElements} because it's one of the
 * {@link XsdAbstractElement} concrete classes that can have a {@link XsdNamedElements#name} attribute.
 *
 * @see <a href="https://www.w3schools.com/xml/el_group.asp">xsd:group description and usage at w3c</a>
 */
public class XsdGroup extends XsdNamedElements {

    public static final String XSD_TAG = "xsd:group";
    public static final String XS_TAG = "xs:group";

    /**
     * {@link XsdGroupVisitor} instance which restricts his children to {@link XsdAll}, {@link XsdChoice} or
     * {@link XsdSequence} instances.
     * Can also have {@link XsdAnnotation} children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
     */
    private XsdGroupVisitor visitor = new XsdGroupVisitor(this);

    /**
     * The child element of the {@link XsdGroup} instance. It can be a {@link XsdAll}, {@link XsdChoice} or a
     * {@link XsdSequence} instance.
     */
    private XsdMultipleElements childElement;

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

    private XsdGroup(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap) {
        super(parser, attributesMap);

        this.minOccurs = AttributeValidations.validateNonNegativeInteger(XSD_TAG, MIN_OCCURS_TAG, attributesMap.getOrDefault(MIN_OCCURS_TAG, "1"));
        this.maxOccurs = AttributeValidations.maxOccursValidation(XSD_TAG, attributesMap.getOrDefault(MAX_OCCURS_TAG, "1"));
    }

    private XsdGroup(XsdAbstractElement parent, @NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap) {
        this(parser, attributesMap);
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
    }

    /**
     * Asserts if the current object has the name attribute when not being a direct child of the XsdSchema element, which is
     * not allowed, throwing an exception in that case.
     */
    private void rule2() {
        if (!(parent instanceof XsdSchema) && name != null){
            throw new ParsingException(XSD_TAG + " element: The " + NAME_TAG + " should only be used when the parent of the " + XSD_TAG + " is the " + XsdSchema.XSD_TAG + " element." );
        }
    }

    /**
     * Asserts if the current has no value for its name attribute while being a direct child of the top level XsdSchema element,
     * which is required. Throws an exception if no name is present.
     */
    private void rule3() {
        if (parent instanceof XsdSchema && name == null){
            throw new ParsingException(XSD_TAG + " element: The " + NAME_TAG + " should is required the parent of the " + XSD_TAG + " is the " + XsdSchema.XSD_TAG + " element." );
        }
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public XsdGroupVisitor getVisitor() {
        return visitor;
    }

    /**
     * @return A list with the child element of the {@link XsdGroup} instance.
     */
    @Override
    public List<ReferenceBase> getElements() {
        List<ReferenceBase> list = new ArrayList<>();

        if (childElement != null){
            list.add(ReferenceBase.createFromXsd(childElement));
        }

        return list;
    }

    /**
     * Performs a copy of the current object for replacing purposes. The cloned objects are used to replace
     * {@link UnsolvedReference} objects in the reference solving process.
     * @param placeHolderAttributes The additional attributes to add to the clone.
     * @return A copy of the object from which is called upon.
     */
    @Override
    public XsdNamedElements clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(attributesMap);
        placeHolderAttributes.remove(REF_TAG);

        XsdGroup elementCopy = new XsdGroup(this.parent, this.parser, placeHolderAttributes);

        if (childElement != null){
            elementCopy.setChildElement(this.childElement);
        }

        return elementCopy;
    }

    public void setChildElement(XsdMultipleElements childElement) {
        this.childElement = childElement;
        childElement.getElements().forEach(childElementObj -> childElementObj.getElement().setParent(childElement));
        this.childElement.setParent(this);
    }

    @SuppressWarnings("unused")
    public XsdMultipleElements getChildElement() {
        return childElement;
    }

    /**
     * @return The childElement as a {@link XsdAll} object or null if childElement isn't a {@link XsdAll} instance.
     */
    @SuppressWarnings("unused")
    public XsdAll getChildAsAll() {
        return XsdMultipleElements.getChildAsdAll(childElement);
    }

    /**
     * @return The childElement as a {@link XsdChoice} object or null if childElement isn't a {@link XsdChoice} instance.
     */
    @SuppressWarnings("unused")
    public XsdChoice getChildAsChoice() {
        return XsdMultipleElements.getChildAsChoice(childElement);
    }

    /**
     * @return The childElement as a {@link XsdSequence} object or null if childElement isn't a {@link XsdSequence} instance.
     */
    @SuppressWarnings("unused")
    public XsdSequence getChildAsSequence() {
        return XsdMultipleElements.getChildAsSequence(childElement);
    }

    public static ReferenceBase parse(@NotNull XsdParserCore parser, Node node){
        return xsdParseSkeleton(node, new XsdGroup(parser, convertNodeMap(node.getAttributes())));
    }

    public Integer getMinOccurs() {
        return minOccurs;
    }

    public String getMaxOccurs() {
        return maxOccurs;
    }

}
