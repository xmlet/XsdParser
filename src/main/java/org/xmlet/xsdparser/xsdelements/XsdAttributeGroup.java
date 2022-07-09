package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    public static final String TAG = "attributeGroup";

    /**
     * A list of {@link XsdAttributeGroup} children instances.
     */
    //This list is populated by the replaceUnsolvedElements and never directly (such as a Visitor method like all else).
    //The UnsolvedReference is placed in the XsdParser queue by the default implementation of the Visitor#visit(XsdAttributeGroup element)
    //The reference solving process then sends the XsdAttributeGroup to this class.
    private List<ReferenceBase> attributeGroups = new ArrayList<>();

    /**
     * A list of {@link XsdAttribute} children instances.
     */
    private List<ReferenceBase> attributes = new ArrayList<>();

    private XsdAttributeGroup(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, attributesMap, visitorFunction);
    }

    private XsdAttributeGroup(XsdAbstractElement parent, @NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, attributesMap, visitorFunction);
        setParent(parent);
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    /**
     * @return A list of all {@link XsdAttribute} objects contained in the current {@link XsdAttributeGroup} instance,
     * either directly or present in its children {@link XsdAttributeGroup} in the
     * {@link XsdAttributeGroup#attributeGroups} field.
     */
    @Override
    public List<ReferenceBase> getElements() {
        List<ReferenceBase> allAttributes = new ArrayList<>();

        getXsdAttributeGroups().forEach(attributeGroup -> {
            allAttributes.addAll(attributeGroup.getElements());
        });

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
        placeHolderAttributes.putAll(attributesMap);
        placeHolderAttributes.remove(REF_TAG);

        XsdAttributeGroup elementCopy = new XsdAttributeGroup(this.parent, this.parser, placeHolderAttributes, visitorFunction);

        elementCopy.attributes = this.attributes.stream().map(attributeReference -> ReferenceBase.clone(parser, attributeReference, elementCopy)).collect(Collectors.toList());
        elementCopy.attributeGroups = this.attributeGroups.stream().map(attributeGroupReference -> ReferenceBase.clone(parser, attributeGroupReference, elementCopy)).collect(Collectors.toList());

        elementCopy.cloneOf = this;
        elementCopy.parent = null;

        return elementCopy;
    }

    @Override
    public void replaceUnsolvedElements(NamedConcreteElement element) {
        if (element.getElement() instanceof  XsdAttributeGroup){
            Optional<ReferenceBase> attributeGroupUnsolvedReference = attributeGroups.stream().filter(attributeGroup -> attributeGroup instanceof UnsolvedReference && ((UnsolvedReference) attributeGroup).getRef().equals(element.getName())).findFirst();
            if (attributeGroupUnsolvedReference.isPresent()){
                attributeGroups.remove(attributeGroupUnsolvedReference.get());

                attributeGroups.add(element);
            }
        }
    }

    public Stream<XsdAttributeGroup> getXsdAttributeGroups() {
        return attributeGroups
                .stream()
                .filter(element -> element.getElement() instanceof XsdAttributeGroup)
                .map(element -> (XsdAttributeGroup) element.getElement());
    }

    public Stream<XsdAttributeGroup> getAllXsdAttributeGroups() {
        List<XsdAttributeGroup> a = new ArrayList<>();

        for(XsdAttributeGroup attributeGroup: attributeGroups
                .stream()
                .filter(element -> element.getElement() instanceof XsdAttributeGroup)
                .map(element -> (XsdAttributeGroup) element.getElement())
                .collect(Collectors.toList())){

            a.add(attributeGroup);

            a.addAll(attributeGroup.getAllXsdAttributeGroups().collect(Collectors.toList()));
        }

        return a.stream();
    }

    /**
     * @return All the attributes of this attributeGroup and other attributeGroups contained within.
     */
    public Stream<XsdAttribute> getXsdAttributes(){
        return attributes
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

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdAttributeGroup(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    public void addAttribute(ReferenceBase attribute) {
        attributes.add(attribute);
    }

    public void addAttributeGroup(ReferenceBase attributeGroup) {
        attributeGroups.add(attributeGroup);
    }
}
