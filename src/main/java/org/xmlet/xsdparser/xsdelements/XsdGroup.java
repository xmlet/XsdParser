package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.enums.EnumUtils;
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

    private XsdGroup(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    private XsdGroup(XsdAbstractElement parent, @NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
        setParent(parent);
    }

    /**
     * Sets the occurs fields either with the Map values or with their default values.
     * @param elementFieldsMapParam The Map object containing the information previously contained in the Node object.
     */
    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.minOccurs = EnumUtils.minOccursValidation(elementFieldsMap.getOrDefault(MIN_OCCURS_TAG, "1"));
        this.maxOccurs = EnumUtils.maxOccursValidation(elementFieldsMap.getOrDefault(MAX_OCCURS_TAG, "1"));
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
        placeHolderAttributes.putAll(elementFieldsMap);
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

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node){
        return xsdParseSkeleton(node, new XsdGroup(parser, convertNodeMap(node.getAttributes())));
    }

    public Integer getMinOccurs() {
        return minOccurs;
    }

    public String getMaxOccurs() {
        return maxOccurs;
    }

}
