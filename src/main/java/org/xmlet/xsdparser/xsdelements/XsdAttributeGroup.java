package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAttributeGroupVisitor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A class is representing xsd:attributeGroup elements. It can have a ref attribute and therefore extends from
 * {@link XsdNamedElements}, which serves as a base to every element type that can have a ref attribute. For more
 * information check {@link XsdNamedElements}.
 *
 * @see <a href="https://www.w3schools.com/xml/el_attributegroup.asp">xsd:attributeGroup element description and usage at w3c</a>
 */
public class XsdAttributeGroup extends XsdNamedElements {

    public static final String XSD_TAG = "xsd:attributeGroup";
    public static final String XS_TAG = "xs:attributeGroup";

    /**
     * {@link XsdAttributeGroupVisitor} instance which limits its children to {@link XsdAttribute} instances.
     * Can also have {@link XsdAnnotation} as children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
     */
    private final XsdAttributeGroupVisitor visitor = new XsdAttributeGroupVisitor(this);

    /**
     * A list of {@link XsdAttributeGroup} children instances.
     */
    //This list is populated by the replaceUnsolvedElements and never directly (such as a Visitor method like all else).
    //The UnsolvedReference is placed in the XsdParser queue by the default implementation of the Visitor#visit(XsdAttributeGroup element)
    //The reference solving process then sends the XsdAttributeGroup to this class.
    private List<XsdAttributeGroup> attributeGroups = new ArrayList<>();

    /**
     * A list of {@link XsdAttribute} children instances.
     */
    private List<ReferenceBase> attributes = new ArrayList<>();

    private XsdAttributeGroup(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    private XsdAttributeGroup(XsdAbstractElement parent, @NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
        setParent(parent);
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public XsdAttributeGroupVisitor getVisitor() {
        return visitor;
    }

    /**
     * @return A list of all {@link XsdAttribute} objects contained in the current {@link XsdAttributeGroup} instance,
     * either directly or present in its children {@link XsdAttributeGroup} in the
     * {@link XsdAttributeGroup#attributeGroups} field.
     */
    @Override
    public List<ReferenceBase> getElements() {
        List<ReferenceBase> allAttributes = new ArrayList<>();

        attributeGroups.forEach(attributeGroup -> allAttributes.addAll(attributeGroup.getElements()));

        allAttributes.addAll(attributes);

        return allAttributes;
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

        XsdAttributeGroup elementCopy = new XsdAttributeGroup(this.parent, this.parser, placeHolderAttributes);

        elementCopy.attributes.addAll(this.attributes);
        elementCopy.attributeGroups.addAll(this.attributeGroups);

        return elementCopy;
    }

    @Override
    public void replaceUnsolvedElements(NamedConcreteElement element) {
        if (element.getElement() instanceof  XsdAttributeGroup){
            XsdAttributeGroup attributeGroup = (XsdAttributeGroup) element.getElement();

            attributeGroup.attributes.forEach(attribute -> attribute.getElement().setParent(attributeGroup));

            this.attributeGroups.add(attributeGroup);
        }
    }

    @SuppressWarnings("unused")
    public List<XsdAttributeGroup> getAttributeGroups() {
        return attributeGroups;
    }

    /**
     * @return All the attributes of this attributeGroup and other attributeGroups contained within.
     */
    @SuppressWarnings("unused")
    public Stream<XsdAttribute> getAllAttributes(){
        return getElements()
                .stream()
                .filter(element -> element.getElement() instanceof XsdAttribute)
                .map(element -> (XsdAttribute) element.getElement());
    }

    /**
     * @return The attributes directly defined in this attributeGroup.
     */
    @SuppressWarnings("unused")
    public Stream<XsdAttribute> getDirectAttributes(){
        return attributes
                    .stream()
                    .filter(element -> element.getElement() instanceof XsdAttribute)
                    .map(element -> (XsdAttribute) element.getElement());
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node) {
        return xsdParseSkeleton(node, new XsdAttributeGroup(parser, convertNodeMap(node.getAttributes())));
    }

    public void addAttribute(ReferenceBase attribute) {
        attributes.add(attribute);
    }
}
